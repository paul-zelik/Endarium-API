package net.endarium.api.players.report;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.endarium.api.EndariumCommons;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportManager {
    private static final String REPORTS_KEY = "Endarium:Reports";
    private String REPORT_PREFIX_ID = "Report:ReportID";

    private EndariumCommons endariumCommons;
    private Gson gson;
    private ReportIDPool reportIDPool;
    private Type reportListType;

    public ReportManager() {
        this.endariumCommons = EndariumCommons.getInstance();
        this.gson = new Gson();
        this.reportListType = new TypeToken<List<Report>>() {}.getType();
        this.reportIDPool = new ReportIDPool();
    }

    public boolean addReport(Report report) {
        try (Jedis jedis = endariumCommons.getJedisPool().getResource()) {
            String serializedReport = gson.toJson(report);
            String key = REPORTS_KEY + ":" + report.getID(); // Utilisez une clé unique pour chaque rapport
            jedis.setex(key, 30 * 60, serializedReport); // 30 minutes d'expiration
        } catch (Exception e) {
            e.printStackTrace(); // Ou logger l'exception avec un logger
            return false;
        }
        return true;
    }


    public List<Report> getReports() {
        List<Report> reports = new ArrayList<>();
        try (Jedis jedis = endariumCommons.getJedisPool().getResource()) {
            Set<String> reportKeys = jedis.keys("Endarium:Reports:*");
            for (String reportKey : reportKeys) {
                String serializedReport = jedis.get(reportKey);
                if (serializedReport != null) {
                    Report report = gson.fromJson(serializedReport, Report.class);
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    public void removeReport(Report report) {
        try (Jedis jedis = endariumCommons.getJedisPool().getResource()) {
            String serializedReport = gson.toJson(report);
            Pipeline pipeline = jedis.pipelined();
            pipeline.lrem(REPORTS_KEY, 1, serializedReport);
            pipeline.sync();
        }
    }

    /**
     * Récupérer le Système de gestion des ID de Groupe.
     */
    public ReportIDPool getPartyIDPool() {
        return reportIDPool;
    }

    /**
     * Gestion des ID de Groupes.
     */
    private class ReportIDPool {

        /**
         * Générer un ID de Groupe.
         */
        public int nextId() {
            try (Jedis jedis = endariumCommons.getJedisPool().getResource()) {

                Set<String> used = jedis.smembers(REPORT_PREFIX_ID);
                if (used == null)
                    return storeId(0);

                Set<Integer> ids = used.stream().map(i -> Integer.valueOf(i)).collect(Collectors.toSet());
                int id = 0;
                do {
                    id++;
                } while (ids.contains(id));
                return storeId(id);
            }
        }

        /**
         * Sauvegarder un ID de Serveur sous Redis.
         *
         * @param id
         */
        private int storeId(int id) {
            try (Jedis jedis = endariumCommons.getJedisPool().getResource()) {
                jedis.sadd(REPORT_PREFIX_ID, id + "");
            }
            return id;
        }

        /**
         * Supprimer un ensemble de key de Groupe.
         *
         * @param id
         */
        public void returnId(int id) {
            try (Jedis jedis = endariumCommons.getJedisPool().getResource()) {
                jedis.srem(REPORT_PREFIX_ID, id + "");
            }
        }
    }
}
