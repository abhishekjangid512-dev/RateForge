package com.rateforge.apikey.enums;

public enum Plan {
    FREE(10), PRO(100), ENTERPRISE(1000);

    private final int requestsPerMinute;

    Plan(int requestsPerMinute) { this.requestsPerMinute = requestsPerMinute; }

    public int getRequestsPerMinute() { return requestsPerMinute; }
}
