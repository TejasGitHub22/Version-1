package com.coffee.coffeeApp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FacilityUsageSummaryDto {
    public static class UsageRow {
        private String brewType;
        private long count;
        private LocalDateTime timestamp;

        public UsageRow() {
        }

        public UsageRow(String brewType, long count, LocalDateTime timestamp) {
            this.brewType = brewType;
            this.count = count;
            this.timestamp = timestamp;
        }

        public String getBrewType() {
            return brewType;
        }

        public void setBrewType(String brewType) {
            this.brewType = brewType;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }

    private List<UsageRow> rows;
    private LocalDateTime since;
    private LocalDateTime refreshedAt;

    public FacilityUsageSummaryDto() {
    }

    public FacilityUsageSummaryDto(List<UsageRow> rows, LocalDateTime since) {
        this.rows = rows;
        this.since = since;
        this.refreshedAt = LocalDateTime.now();
    }

    public List<UsageRow> getRows() {
        return rows;
    }

    public void setRows(List<UsageRow> rows) {
        this.rows = rows;
    }

    public LocalDateTime getSince() {
        return since;
    }

    public void setSince(LocalDateTime since) {
        this.since = since;
    }

    public LocalDateTime getRefreshedAt() {
        return refreshedAt;
    }

    public void setRefreshedAt(LocalDateTime refreshedAt) {
        this.refreshedAt = refreshedAt;
    }
}


