package kr.sesac.wordcounter.model;

public class AnalysisConfig {

    private static final String[] DEFAULT_CSV_COLUMNS = {"text"};
    private final String[] csvColumns;

    public AnalysisConfig(String[] csvColumns) {
        this.csvColumns = csvColumns;
    }

    public static AnalysisConfig defaultConfig() {
        return new AnalysisConfig(DEFAULT_CSV_COLUMNS);
    }

    public String[] getCsvColumns() {
        return csvColumns;
    }
}
