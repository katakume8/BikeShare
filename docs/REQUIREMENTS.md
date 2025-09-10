# BikeShare System Requirements Specification

## 1. System Overview

The BikeShare System is a bike rental platform that allows users to rent bikes from various stations across a city. The system manages users, bikes, stations, and rental transactions with automated billing and tracking capabilities.

## 2. Functional Requirements

### 2.1 User Management

**FR-1.1 User Registration**
- The system SHALL allow users to register with email, name, and phone number
- Email addresses MUST be unique and follow valid email format (RFC 5322)
- Names MUST be between 2-50 characters and contain only letters, spaces, hyphens, and apostrophes
- Phone numbers MUST follow Swedish national format (07X-XXX XX XX) or international format (+46 7X XXX XX XX)
- Users MUST accept terms and conditions during registration

**FR-1.2 User Profile Management**
- Users SHALL be able to update their profile information
- Users SHALL be able to change their password (minimum 8 characters, containing uppercase, lowercase, and numbers)
- The system SHALL maintain user registration timestamp and last login information

**FR-1.3 Account Balance Management**
- Users SHALL be able to add funds to their account
- Minimum fund addition: 0.10 SEK
- Maximum fund addition: 10,000.00 SEK per transaction
- Maximum account balance: 20,000.00 SEK
- The system SHALL maintain transaction history for all balance changes

**FR-1.4 Membership Types**
- The system SHALL support multiple membership types: BASIC, PREMIUM, VIP, STUDENT, CORPORATE
- Each membership type SHALL have different discount rates and benefits
- Membership upgrades SHALL be effective immediately
- Membership downgrades SHALL be effective at the next billing cycle

### 2.2 Bike Management

**FR-2.1 Bike Types**
- The system SHALL support four bike types: STANDARD, ELECTRIC, MOUNTAIN, CARGO
- Each bike type SHALL have different pricing per minute:
  - STANDARD: 0.50 SEK/minute
  - ELECTRIC: 1.00 SEK/minute
  - MOUNTAIN: 0.70 SEK/minute
  - CARGO: 1.20 SEK/minute

**FR-2.2 Bike States**
- Bikes SHALL have the following states: AVAILABLE, RENTED, MAINTENANCE, OUT_OF_SERVICE
- State transitions SHALL be logged with timestamp and reason
- Only AVAILABLE bikes can be rented
- Bikes in MAINTENANCE state SHALL have an associated reason and estimated completion time

**FR-2.3 Bike Properties**
- Each bike SHALL have a unique identifier (alphanumeric, 6-12 characters)
- Electric bikes SHALL have battery level tracking (0-100%)
- All bikes SHALL track total distance traveled and number of rides
- Bikes SHALL have a current station assignment when not rented

### 2.3 Station Management

**FR-3.1 Station Properties**
- Each station SHALL have a unique identifier, name, and geographic coordinates
- Stations SHALL have a fixed capacity (number of bike docks)
- The system SHALL track current bike count at each station
- Station coordinates MUST be valid latitude (-90 to 90) and longitude (-180 to 180)

**FR-3.2 Station Operations**
- The system SHALL prevent adding bikes to stations at full capacity
- The system SHALL prevent removing bikes from empty stations
- Station capacity changes SHALL require administrative approval
- The system SHALL track station usage statistics (bikes added/removed, popular times)

**FR-3.3 Geographic Services**
- The system SHALL calculate distances between stations using Haversine formula
- Users SHALL be able to find nearest stations within specified radius
- The system SHALL provide station density analysis for city planning

### 2.4 Ride Management

**FR-4.1 Ride Lifecycle**
- Users SHALL be able to start a ride by selecting an available bike at a station
- Users can have a maximum of 1 active ride at any time
- Rides SHALL have start time, start station, bike identifier, and user identifier
- Ride completion SHALL require returning bike to any station with available capacity

**FR-4.2 Ride Billing**
- Ride cost SHALL be calculated as: duration_minutes × bike_type_rate × (1 - membership_discount)
- Billing SHALL occur upon ride completion
- Users MUST have sufficient balance to cover minimum ride cost (5 minutes)
- The system SHALL handle partial payments and negative balances gracefully

**FR-4.3 Ride Restrictions**
- Maximum ride duration: 24 hours (automatically ended with penalty fee)
- Rides longer than 2 hours SHALL incur additional hourly charges
- Lost bike reporting SHALL stop billing and mark bike for investigation

### 2.5 Payment and Billing

**FR-5.1 Payment Processing**
- The system SHALL support credit card payments for adding funds
- Payment transactions SHALL be atomic (all-or-nothing)
- Failed payments SHALL not affect user balance
- The system SHALL integrate with external payment service (PaymentService interface)

**FR-5.2 Billing Rules**
- Membership discounts SHALL apply to all ride charges
- Peak hour surcharges (25% extra) SHALL apply during 7-9 AM and 5-7 PM on weekdays
- Free minutes from membership SHALL be deducted before billing
- The system SHALL generate monthly billing statements

### 2.6 Notification System

**FR-6.1 User Notifications**
- Users SHALL receive notifications for: low balance, ride started, ride completed, membership changes
- Notifications SHALL be sent via email and optionally SMS
- Users SHALL be able to configure notification preferences
- The system SHALL retry failed notifications up to 3 times

**FR-6.2 System Alerts**
- The system SHALL generate alerts for: bike maintenance needs, station capacity issues, payment failures
- Administrative alerts SHALL be sent immediately to system operators
- Alert history SHALL be maintained for auditing purposes

## 3. Non-Functional Requirements

### 3.1 Performance Requirements

**NFR-1.1 Response Time**
- User login: < 2 seconds
- Bike rental start: < 3 seconds
- Station search: < 1 second
- Ride completion: < 5 seconds

**NFR-1.2 Throughput**
- The system SHALL support 1000 concurrent users
- The system SHALL handle 10,000 ride transactions per hour
- Database queries SHALL complete within 500ms for 95% of requests

**NFR-1.3 Availability**
- System uptime: 99.5% (maximum 43.2 hours downtime per year)
- Planned maintenance windows: maximum 4 hours per month
- Automatic failover for critical services within 30 seconds

### 3.2 Security Requirements

**NFR-2.1 Authentication**
- User passwords SHALL be hashed using bcrypt with minimum cost factor 12
- Login sessions SHALL expire after 24 hours of inactivity
- The system SHALL implement rate limiting for login attempts
- The system SHALL support BankID authentication for Swedish users
- Swedish personnummer (personal identity numbers) SHALL be validated using the Luhn algorithm
- Personnummer format SHALL follow YYMMDD-NNNN pattern (e.g., 800101-8129)
- The system SHALL validate that the last digit is a correct check digit per Luhn algorithm

**NFR-2.2 Swedish Identity Validation**
- The system SHALL validate Swedish personnummer using the Luhn algorithm
- Personnummer format: YYMMDD-NNNN (e.g., 800101-8129)
- Validation algorithm steps:
  1. Multiply digits at positions 1,3,5,7,9 by 2
  2. Multiply digits at positions 2,4,6,8,10 by 1  
  3. Sum all resulting digits (if multiplication result > 9, sum the digits)
  4. Calculate checksum: (10 - (sum mod 10)) mod 10
  5. Verify checksum equals the last digit
- Example validation for 800101-8129:
  - 8×2=16 (1+6=7), 0×1=0, 0×2=0, 1×1=1, 0×2=0, 1×1=1, 8×2=16 (1+6=7), 1×1=1, 2×2=4
  - Sum: 7+0+0+1+0+1+7+1+4=21
  - Checksum: (10-(21 mod 10)) mod 10 = (10-1) mod 10 = 9
  - Valid: checksum (9) matches last digit (9)

**NFR-2.3 Data Protection**
- Personal data SHALL be encrypted at rest using AES-256
- API communications SHALL use TLS 1.3 or higher
- Credit card information SHALL NOT be stored in system database

### 3.3 Compatibility Requirements

**NFR-3.1 Platform Support**
- The system SHALL run on Java 21 or higher
- Database: PostgreSQL 13+ or H2 for testing
- The system SHALL be containerizable using Docker

**NFR-3.2 Browser Support**
- Web interface SHALL support Chrome 90+, Firefox 88+, Safari 14+
- Mobile responsive design for screens 320px and wider

### 3.4 Scalability Requirements

**NFR-4.1 Data Volume**
- The system SHALL support up to 100,000 registered users
- The system SHALL handle up to 50,000 bikes across 5,000 stations
- The system SHALL maintain 5 years of ride history data

### 3.5 Environmental Requirements

**NFR-5.1 Sustainability**
- The system SHALL monitor and report energy consumption
- Database queries SHALL be optimized to minimize CPU usage
- The system SHALL support green energy indicators for stations

## 4. System Constraints

### 4.1 Technical Constraints

- The system MUST be developed using Java and Spring Framework
- The system MUST use Maven for dependency management
- Unit tests MUST achieve minimum 80% code coverage
- The system MUST follow Clean Architecture principles

### 4.2 Business Constraints

- The system MUST comply with GDPR and Swedish data protection regulations
- Payment processing MUST comply with PCI DSS standards
- The system MUST support Swedish Krona (SEK) as primary currency

### 4.3 Operational Constraints

- System maintenance windows MUST be scheduled outside peak hours (7-9 AM, 5-7 PM)
- Database backups MUST be performed daily with 30-day retention
- System logs MUST be retained for minimum 90 days for auditing

## 5. Testing Requirements

### 5.1 Unit Testing
- All public methods MUST have unit tests
- Critical business logic MUST achieve 100% branch coverage
- Tests MUST be executable in isolation and be deterministic

### 5.2 Integration Testing
- All service integrations MUST be tested with realistic scenarios
- Database operations MUST be tested with H2 in-memory database
- External service dependencies MUST be mocked or stubbed

### 5.3 Performance Testing
- Load testing MUST validate system performance under expected load
- Stress testing MUST identify system breaking points
- Memory leak testing MUST ensure stable long-term operation

### 5.4 Security Testing
- Input validation MUST prevent SQL injection and XSS attacks
- Authentication and authorization MUST be thoroughly tested
- Sensitive data handling MUST be verified for compliance

## 6. Acceptance Criteria

### 6.1 Feature Completion
- All functional requirements MUST be implemented and tested
- All APIs MUST be documented with OpenAPI specification
- User interface MUST be intuitive and accessible (WCAG 2.1 AA)

### 6.2 Quality Gates
- Code coverage: minimum 80% line coverage, 70% branch coverage
- Static analysis: zero critical security vulnerabilities
- Performance: all response time requirements met under load testing

### 6.3 Documentation
- System architecture documentation MUST be complete
- User manual MUST cover all features
- Administrator guide MUST cover deployment and maintenance

---

**Document Version**: 1.0  
**Last Updated**: August 29, 2025  
**Approved By**: Course Instructor  
**Classification**: Educational Use
