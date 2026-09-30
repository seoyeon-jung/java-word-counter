package kr.sesac.wordcounter.parser;

import kr.sesac.wordcounter.analysis.WordCounter;
import kr.sesac.wordcounter.model.AnalysisConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FileParser {

    private static final String[] TSV_COLUMNS = {"document"};
    private static final String HTML_SELECTOR = "#content";

    private FileParser() {}

    public static void parse(Path input, Map<String, Long> wordCounts, AnalysisConfig config) throws IOException {

        String fileName = input.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".txt")) {
            parseTxt(input, wordCounts);

        } else if (fileName.endsWith(".csv")) {
            parseDelimited(
                    input,
                    wordCounts,
                    CSVFormat.DEFAULT,
                    config.getCsvColumns()
            );

        } else if (fileName.endsWith(".tsv")) {
            CSVFormat tsvFormat = CSVFormat.DEFAULT.builder().setDelimiter('\t').setQuote(null).get();

            parseDelimited(input, wordCounts, tsvFormat, TSV_COLUMNS);

        } else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            parseHtml(input, wordCounts);
        } else {
            throw new IOException("지원하지 않는 파일 형식입니다: " + input);
        }
    }

    private static void parseTxt(Path input, Map<String, Long> wordCounts) throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                WordCounter.countWords(line, wordCounts);
            }
        }
    }

    private static void parseDelimited(Path input, Map<String, Long> wordCounts,
                                       CSVFormat baseFormat, String[] requiredColumns) throws IOException {

        CSVFormat format = baseFormat.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .get();

        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {

            validateHeaders(parser, requiredColumns);

            int headerCount = parser.getHeaderNames().size();

            for (CSVRecord record : parser) {
                validateRecordWidth(record, headerCount);

                for (String column : requiredColumns) {
                    String actualHeader = findActualHeader(parser, column);

                    WordCounter.countWords(record.get(actualHeader), wordCounts
                    );
                }
            }
        }
    }

    private static void validateHeaders(CSVParser parser, String[] requiredColumns) throws IOException {

        List<String> headers = parser.getHeaderNames();

        if (headers.isEmpty()) {
            throw new IOException("헤더가 없습니다.");
        }

        for (String requiredColumn : requiredColumns) {
            findActualHeader(parser, requiredColumn);
        }
    }

    private static String findActualHeader(CSVParser parser, String requiredColumn) throws IOException {

        for (String header : parser.getHeaderNames()) {
            if (header.trim().equals(requiredColumn)) {
                return header;
            }
        }

        throw new IOException("필수 열이 없습니다: " + requiredColumn);
    }

    private static void validateRecordWidth(CSVRecord record, int headerCount) throws IOException {
        if (record.size() != headerCount) {
            throw new IOException("헤더와 데이터의 열 개수가 일치하지 않습니다.");
        }
    }

    private static void parseHtml(Path input, Map<String, Long> wordCounts) throws IOException {

        Document document = Jsoup.parse(input.toFile(), StandardCharsets.UTF_8.name());

        Elements contents = document.select(HTML_SELECTOR);

        if (contents.size() != 1) {
            throw new IOException("HTML 본문 요소를 정확히 하나 찾을 수 없습니다. "
                            + "선택자: "
                            + HTML_SELECTOR
                            + ", 찾은 개수: "
                            + contents.size()
            );
        }

        Element content = contents.getFirst();

        content.select("header, nav, footer, script, style").remove();

        WordCounter.countWords(content.text(), wordCounts);
    }
}