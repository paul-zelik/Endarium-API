package net.endarium.api.players.report;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Report {
    private String reason;
    private Date date;

    private String id;
    private UUID reportedPlayerUUID;
    private UUID reporterUUID;
    private LocalDateTime reportDate;


    public Report(UUID reporterUUID, UUID reportedPlayerUUID, String reason) {
        this.id = UUID.randomUUID().toString();
        this.reason = reason;
        this.reportedPlayerUUID = reportedPlayerUUID;
        this.reporterUUID = reporterUUID;
        this.reportDate = LocalDateTime.now();
    }

    /**
     * Récupérer l'ID de la Party.
     *
     * @return
     */
    public String getID() {
        return id;
    }

    // Getters and setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UUID getReportedPlayerUUID() {
        return reportedPlayerUUID;
    }

    public void setReportedPlayerUUID(UUID reportedPlayerUUID) {
        this.reportedPlayerUUID = reportedPlayerUUID;
    }

    public UUID getReporterUUID() {
        return reporterUUID;
    }

    public void setReporterUUID(UUID reporterUUID) {
        this.reporterUUID = reporterUUID;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

}
