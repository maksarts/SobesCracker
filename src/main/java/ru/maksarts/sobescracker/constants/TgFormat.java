package ru.maksarts.sobescracker.constants;

public enum TgFormat {
    PARSE_MODE_MARKDOWN ("MarkdownV2"),
    PARSE_MODE_HTML ("HTML");

    private final String tag;

    public String getTag(){
        return tag;
    }
    TgFormat(String tag) {
        this.tag = tag;
    }
}
