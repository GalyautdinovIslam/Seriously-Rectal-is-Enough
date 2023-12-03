package ru.itis.slakeeper.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromAnswer {
    private String status;
    private PromData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PromData {
        private String resultType;
        private List<Result> result;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Result {
        private Metric metric;
        private List<Object> value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Metric {
        private String instance;
        private String job;
    }

    public LocalDateTime getTime() {
        try {
            return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(
                            (long) (((double) getData().getResult().get(0).getValue().get(0)) * 1000)
                    ),
                    TimeZone.getDefault().toZoneId()
            );
        } catch (Exception exception) {
            return LocalDateTime.now();
        }
    }

    public double getValue() {
        try {
            return Double.parseDouble(String.valueOf(getData().getResult().get(0).getValue().get(1)));
        } catch (Exception exception) {
            return 0;
        }
    }
}
