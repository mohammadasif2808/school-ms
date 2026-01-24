package com.school.academic.frontoffice.dto.halfday;

import com.school.academic.frontoffice.dto.PageMetadata;

import java.util.List;

/**
 * Paginated response for half day notices.
 */
public class HalfDayNoticePageResponse {

    private List<HalfDayNoticeResponse> content;
    private PageMetadata page;

    public HalfDayNoticePageResponse() {
    }

    public HalfDayNoticePageResponse(List<HalfDayNoticeResponse> content, PageMetadata page) {
        this.content = content;
        this.page = page;
    }

    // Getters and Setters
    public List<HalfDayNoticeResponse> getContent() {
        return content;
    }

    public void setContent(List<HalfDayNoticeResponse> content) {
        this.content = content;
    }

    public PageMetadata getPage() {
        return page;
    }

    public void setPage(PageMetadata page) {
        this.page = page;
    }
}
