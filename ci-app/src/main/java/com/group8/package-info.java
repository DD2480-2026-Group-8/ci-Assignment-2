/**
 * Core CI server implementation for DD2480 Assignment 2.
 * <p>
 * This package contains the main classes for a minimal continuous integration
 * server that:
 * </p>
 * <ul>
 * <li><b>P1 – Compilation:</b> Receives GitHub webhook push events and
 * compiles the target project using Maven.</li>
 * <li><b>P2 – Testing:</b> Runs the project's automated tests
 * ({@code mvn test}).</li>
 * <li><b>P3 – Notification:</b> Reports build results as GitHub commit
 * statuses via the REST API.</li>
 * <li><b>P7 – Build history:</b> Persists build records to disk and exposes
 * them via HTTP endpoints for browsing past builds.</li>
 * </ul>
 * <p>
 * The main entry point is {@link com.group8.ContinuousIntegrationServer},
 * which runs a Jetty server listening for webhooks on {@code POST /webhook}.
 * </p>
 */
package com.group8;
