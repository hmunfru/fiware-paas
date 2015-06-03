Feature: As a FIWARE user
         I want to check some security and auth restrictions when using PaaS-Manager features
         so that I can work with blueprints safely


  Scenario Outline: Create new environment with invalid Content-Type header
    Given a content-type header value "<content_type>"
    When  I request the creation of an environment with data:
          | name   | description |
          | nameqa | descqa      |
    Then  I receive an "Unsupported media type" response

    Examples:
    | content_type      |
    | application/xml3  |
    | application/lalal |
    | application/json1 |
    | text/plain        |


  @skip @CLAUDIA-4505
  Scenario Outline: Create new environment with invalid Accept header
    Given an accept header value "<accept_header>"
    When  I request the creation of an environment with data:
          | name   | description |
          | nameqa | descqa      |
    Then  I receive an "Not acceptable" response

    Examples:
    | accept_header     |
    | application/xml3  |
    | application/lalal |
    | application/json1 |
    | text/plain        |


  @auth
  Scenario Outline: Create new environment with incorrect authentication: token
    Given the authentication token "<token>"
    When  I request the creation of an environment with data:
          | name   | description |
          | nameqa | descqa      |
    Then  I receive an "Unauthorized" response

    Examples:

    | token                            |
    | hello world                      |
    | 891855f21b2f1567afb966d3ceee1295 |
    |                                  |


  @auth
  Scenario Outline: Create new environment with incorrect authentication: Tenant-Id
    Given the authentication tenant-id "<tenant_id>"
    When  I request the creation of an environment with data:
          | name   | description |
          | nameqa | descqa      |
    Then  I receive an "Unauthorized" response
    Examples:

    | tenant_id                        |
    | hello world                      |
    | 00001231234112                   |
    |                                  |
