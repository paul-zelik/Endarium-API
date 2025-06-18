package net.endarium.api.utils.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Gestion des Cuboid.
 */
public class Cuboid {

	private String worldname;
	private Vector minimumPoint;
	private Vector maximumPoint;
	public Location minimumLoc, MaximumLoc;

	public Cuboid(Location minimum, Location maximum) {
		if (minimum == null || maximum == null) {
			throw new NullArgumentException("location minmum || location maximun is null");
		}
		if (minimum.getWorld() != maximum.getWorld()) {
			throw new IllegalStateException("world1 != world2");
		}
		this.worldname = minimum.getWorld().getName();
		this.minimumPoint = new Vector(Math.min(minimum.getX(), maximum.getX()),
				Math.min(minimum.getY(), maximum.getY()), Math.min(minimum.getZ(), maximum.getZ()));
		this.maximumPoint = new Vector(Math.max(minimum.getX(), maximum.getX()),
				Math.max(minimum.getY(), maximum.getY()), Math.max(minimum.getZ(), maximum.getZ()));
		this.minimumLoc = minimum;
		this.MaximumLoc = maximum;
	}

	public boolean IsArena(Location location) {
		return location != null && location.getWorld().getName().equals(this.getWorldname())
				&& location.toVector().isInAABB(this.getMinimumPoint(), this.getMaximumPoint());
	}

	public Location getRandomSolidBlockHighest(Location loc1, Location loc2) {
		List<Location> locs = new ArrayList<>();
		Random random = new Random();

		int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

		int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

		for (int x = xMin; x <= xMax; x++) {
			for (int z = zMin; z <= zMax; z++) {
				Block block = loc1.getWorld().getHighestBlockAt(x, z);
				if (!block.getType().isSolid()) {
					locs.add(block.getLocation());
				}
			}
		}

		if (locs.size() == 0)
			return null;
		Location locationtoreturn = locs.get(random.nextInt(locs.size()));
		locationtoreturn.setY(locationtoreturn.getY() + 30);
		return locationtoreturn;
	}

	public void setTypePerY(Material m, int y) {
		World world = Bukkit.getWorld(getWorldname());
		if (world != null) {
			for (int x = this.getMinimumPoint().getBlockX(); x <= this.getMaximumPoint().getBlockX(); x++) {
				for (int z = this.getMinimumPoint().getBlockZ(); z <= this.getMaximumPoint().getBlockZ(); z++) {
					Location loc = new Location(world, x, y, z);
					Block block = loc.getBlock();
					block.setType(m);
				}
			}
		}
	}

	public List<Location> getShower() {
		List<Location> locations = new ArrayList<>();
		World world = Bukkit.getWorld(getWorldname());
		if (world != null) {
			for (int x = this.getMinimumPoint().getBlockX(); x <= this.getMaximumPoint().getBlockX(); x++) {
				for (int z = this.getMinimumPoint().getBlockZ(); z <= this.getMaximumPoint().getBlockZ(); z++) {
					Location loc = new Location(world, x, 20, z);
					locations.add(loc);
				}
			}
		}
		return locations;
	}

	public Location getBlockListWithFilter(Location loc1, Location loc2) {
		List<Location> locs = new ArrayList<>();
		int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		for (int x = xMin; x <= xMax; x++) {
			for (int z = zMin; z <= zMax; z++) {
				Block b = loc1.getWorld().getBlockAt(x, 20/* Y MAX */ , z);
				if (b.getType() == Material.AIR) {
					locs.add(b.getLocation());
				}
			}
		}
		return locs.get(new Random().nextInt(locs.size()));
	}

	public double getLowerX() {
		return this.minimumPoint.getX();
	}

	public double getLowerY() {
		return this.minimumPoint.getY();
	}

	public double getLowerZ() {
		return this.minimumPoint.getZ();
	}

	public double getUpperX() {
		return this.maximumPoint.getX();
	}

	public double getUpperY() {
		return this.maximumPoint.getY();
	}

	public double getUpperZ() {
		return this.maximumPoint.getZ();
	}

	public Location getCenter() {
		int x1 = (int) (this.getUpperX() + 1);
		int y1 = (int) (this.getUpperY() + 1);
		int z1 = (int) (this.getUpperZ() + 1);
		return new Location(Bukkit.getWorld(getWorldname()), this.getLowerX() + (x1 - this.getLowerX()) / 2.0,
				this.getLowerY() + (y1 - this.getLowerY()) / 2.0, this.getLowerZ() + (z1 - this.getLowerZ()) / 2.0);
	}

	public String getWorldname() {
		return worldname;
	}

	public Vector getMinimumPoint() {
		return minimumPoint;
	}

	public Vector getMaximumPoint() {
		return maximumPoint;
	}

	public static Integer replaceBlocks(Location loc1, Location loc2, Material material) {
		int BlockCount = 0;
		List<Block> temp = getBlockList(loc1, loc2);
		for (Block b : temp) {
			if (b.getType() != material) {
				b.setType(material);
				BlockCount++;
			}
		}
		return Integer.valueOf(BlockCount);
	}

	public static Integer replaceBlocksByBlock(Location loc1, Location loc2, Material toReplace, Material New) {
		int BlockCount = 0;
		List<Block> temp = getBlockList(loc1, loc2);
		for (Block b : temp) {
			if ((b.getType() == toReplace) && (b.getType() != New)) {
				b.setType(New);
				BlockCount++;
			}
		}
		return Integer.valueOf(BlockCount);
	}

	public static Integer replaceWalls(Location loc1, Location loc2, Material material) {
		return generateWalls(loc1, loc2, material);
	}

	public static Integer replaceBlocksInWallsByBlock(Location loc1, Location loc2, Material toReplace, Material New) {
		int BlockCount = 0;
		List<Block> temp = getBlockList(loc1, loc2);
		for (Block b : temp) {
			if ((b.getType() == toReplace) && (b.getType() != New)) {
				b.setType(New);
				BlockCount++;
			}
		}
		return Integer.valueOf(BlockCount);
	}

	public static Integer generateWalls(Location loc1, Location loc2, Material material) {
		int BlockCount = 0;

		int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				if (loc1.getWorld().getBlockAt(x, y, zMin).getType() != material) {
					loc1.getWorld().getBlockAt(x, y, zMin).setType(material);
					BlockCount++;
				}
				if (loc1.getWorld().getBlockAt(x, y, zMax).getType() != material) {
					loc1.getWorld().getBlockAt(x, y, zMax).setType(material);
					BlockCount++;
				}
			}
			BlockCount++;
		}
		for (int y = yMin; y <= yMax; y++) {
			for (int z = zMin; z <= zMax; z++) {
				if (loc1.getWorld().getBlockAt(xMin, y, z).getType() != material) {
					loc1.getWorld().getBlockAt(xMin, y, z).setType(material);
					BlockCount++;
				}
				if (loc1.getWorld().getBlockAt(xMax, y, z).getType() != material) {
					loc1.getWorld().getBlockAt(xMax, y, z).setType(material);
					BlockCount++;
				}
			}
		}
		return Integer.valueOf(BlockCount);
	}

	public static void clearCube(Location loc1, Location loc2) {
		List<Block> b = getBlockList(loc1, loc2);
		for (Block bl : b) {
			if (!getWalls(loc1, loc2).contains(bl)) {
				bl.setType(Material.AIR);
			}
		}
	}

	/**
	 * Effectuer un cut de la Zone.
	 * 
	 * @param loc1
	 * @param loc2
	 */
	public static void cutBlock(Location loc1, Location loc2) {
		List<Block> b = getBlockList(loc1, loc2);
		for (Block bl : b) {
			bl.setType(Material.AIR);
		}
	}

	/**
	 * Récupérer les Murs de la Zone.
	 * 
	 * @param loc1
	 * @param loc2
	 * @return
	 */
	public static List<Block> getWalls(Location loc1, Location loc2) {
		List<Block> b = new ArrayList<>();

		int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				b.add(loc1.getWorld().getBlockAt(x, y, zMin));
				b.add(loc1.getWorld().getBlockAt(x, y, zMax));
			}
		}
		for (int y = yMin; y <= yMax; y++) {
			for (int z = zMin; z <= zMax; z++) {
				b.add(loc1.getWorld().getBlockAt(xMin, y, z));
				b.add(loc1.getWorld().getBlockAt(xMax, y, z));
			}
		}
		return b;
	}

	public static List<Block> getBlockList(Location loc1, Location loc2) {
		List<Block> bL = new ArrayList<>();

		int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				for (int z = zMin; z <= zMax; z++) {
					Block b = loc1.getWorld().getBlockAt(x, y, z);
					bL.add(b);
				}
			}
		}
		return bL;
	}

	@SuppressWarnings("unlikely-arg-type")
	public static List<Block> getBlockListWithFilter(Location loc1, Location loc2, List<Block> filter) {
		List<Block> bL = new ArrayList<>();

		int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				for (int z = zMin; z <= zMax; z++) {
					Block b = loc1.getWorld().getBlockAt(x, y, z);
					if (filter.contains(b.getType())) {
						bL.add(b);
					}
				}
			}
		}
		return bL;
	}

	public List<Block> getBlockListWithOutWalls(Location loc1, Location loc2) {
		List<Block> bL = new ArrayList<>();

		int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				for (int z = zMin; z <= zMax; z++) {
					Block b = loc1.getWorld().getBlockAt(x, y, z);
					if (!getWalls(loc1, loc2).contains(b)) {
						bL.add(b);
					}
				}
			}
		}
		return bL;
	}

	@SuppressWarnings("unlikely-arg-type")
	public static List<Block> getBlockListWithFilterWithoutAir(Location loc1, Location loc2, List<Block> filter) {
		List<Block> bL = new ArrayList<>();

		int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				for (int z = zMin; z <= zMax; z++) {
					Block b = loc1.getWorld().getBlockAt(x, y, z);
					if ((filter.contains(b.getType())) && (b.getType() != Material.AIR)) {
						bL.add(b);
					}
				}
			}
		}
		return bL;
	}

	/**
	 * Récupérer tous les blocks de la Zone.
	 * 
	 * @param loc1
	 * @param loc2
	 * @return
	 */
	public static List<Block> getBlockListWithoutAIR(Location loc1, Location loc2) {
		List<Block> bL = new ArrayList<>();

		int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				for (int z = zMin; z <= zMax; z++) {
					Block b = loc1.getWorld().getBlockAt(x, y, z);
					if (b.getType() != Material.AIR) {
						bL.add(b);
					}
				}
			}
		}
		return bL;
	}

	/**
	 * Vérifier si un Block est dans la Zone.
	 * 
	 * @param b
	 * @param corner1
	 * @param corner2
	 * @return
	 */
	public static Boolean isInCube(Block b, Location corner1, Location corner2) {
		int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
		int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
		int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
		int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
		int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
		int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
		if ((b.getLocation().getX() < xMax) && (b.getLocation().getX() > xMin)) {
			if ((b.getLocation().getY() < yMax) && (b.getLocation().getY() > yMin)) {
				if ((b.getLocation().getZ() < zMax) && (b.getLocation().getZ() > zMin)) {
					return Boolean.valueOf(true);
				}
				return Boolean.valueOf(false);
			}
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(false);
	}

	/**
	 * Vérifier si une Location appartient à la Zone.
	 * 
	 * @param loc
	 * @param corner1
	 * @param corner2
	 * @return
	 */
	public static Boolean isInCube(Location loc, Location corner1, Location corner2) {
		int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
		int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
		int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
		int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
		int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
		int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
		if ((loc.getX() < xMax) && (loc.getX() > xMin)) {
			if ((loc.getY() < yMax) && (loc.getY() > yMin)) {
				if ((loc.getZ() < zMax) && (loc.getZ() > zMin)) {
					return Boolean.valueOf(true);
				}
				return Boolean.valueOf(false);
			}
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(false);
	}

	/**
	 * Vérifier si une Entity est dans la Zone.
	 * 
	 * @param ent
	 * @param corner1
	 * @param corner2
	 * @return
	 */
	public static Boolean isInCube(Entity ent, Location corner1, Location corner2) {
		int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
		int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
		int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
		int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
		int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
		int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
		if ((ent.getLocation().getX() < xMax) && (ent.getLocation().getX() > xMin)) {
			if ((ent.getLocation().getY() < yMax) && (ent.getLocation().getY() > yMin)) {
				if ((ent.getLocation().getZ() < zMax) && (ent.getLocation().getZ() > zMin)) {
					return Boolean.valueOf(true);
				}
				return Boolean.valueOf(false);
			}
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(false);
	}

	/**
	 * Récupérer le Centre de la Zone.
	 * 
	 * @param corner1
	 * @param corner2
	 * @return
	 */
	public static Location getCenter(Location corner1, Location corner2) {
		int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
		int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
		int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
		int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
		int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
		int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
		return new Location(corner1.getWorld(), (xMax - xMin) / 2 + xMin, (yMax - yMin) / 2 + yMin,
				(zMax - zMin) / 2 + zMin);
	}
}