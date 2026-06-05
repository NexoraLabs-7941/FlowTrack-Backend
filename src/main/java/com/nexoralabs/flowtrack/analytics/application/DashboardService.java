package com.nexoralabs.flowtrack.analytics.application;

import com.nexoralabs.flowtrack.analytics.interfaces.rest.resources.DashboardResource;

/**
 * Service interface for dashboard operations.
 */
public interface DashboardService {
    
    /**
     * Gets the complete dashboard data including stats, charts, and notifications.
     * @return DashboardResource with all dashboard information
     */
    DashboardResource getDashboardData();
}
