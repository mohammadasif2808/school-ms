package com.school.academic.frontoffice.dto.postal;

import com.school.academic.frontoffice.dto.PageMetadata;

import java.util.List;

/**
 * Paginated response for postal records.
 */
public class PostalRecordPageResponse {

    private List<PostalRecordResponse> content;
    private PageMetadata page;

    public PostalRecordPageResponse() {
    }

    public PostalRecordPageResponse(List<PostalRecordResponse> content, PageMetadata page) {
        this.content = content;
        this.page = page;
    }

    // Getters and Setters
    public List<PostalRecordResponse> getContent() {
        return content;
    }

    public void setContent(List<PostalRecordResponse> content) {
        this.content = content;
    }

    public PageMetadata getPage() {
        return page;
    }

    public void setPage(PageMetadata page) {
        this.page = page;
    }
}
