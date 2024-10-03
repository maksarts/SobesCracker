package ru.maksarts.sobescracker.constants;

public enum TgParseMode {
    PARSE_MODE_MARKDOWN ("MarkdownV2"),
    PARSE_MODE_HTML ("HTML");

    private final String tag;

    public String getTag(){
        return tag;
    }
    TgParseMode(String tag) {
        this.tag = tag;
    }
}
