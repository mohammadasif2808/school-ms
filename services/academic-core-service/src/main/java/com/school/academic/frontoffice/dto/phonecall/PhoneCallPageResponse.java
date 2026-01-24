package com.school.academic.frontoffice.dto.phonecall;

import com.school.academic.frontoffice.dto.PageMetadata;

import java.util.List;

/**
 * Paginated response for phone calls.
 */
public class PhoneCallPageResponse {

    private List<PhoneCallResponse> content;
    private PageMetadata page;

    public PhoneCallPageResponse() {
    }

    public PhoneCallPageResponse(List<PhoneCallResponse> content, PageMetadata page) {
        this.content = content;
        this.page = page;
    }

    // Getters and Setters
    public List<PhoneCallResponse> getContent() {
        return content;
    }

    public void setContent(List<PhoneCallResponse> content) {
        this.content = content;
    }

    public PageMetadata getPage() {
        return page;
    }

    public void setPage(PageMetadata page) {
        this.page = page;
    }
}
