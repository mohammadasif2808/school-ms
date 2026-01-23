Front Office – Data Model

Service: academic-core-service
Module: Front Office
Status: Design / Modeling Phase
Source of Truth: UI Screens (Visitors, Calls, Half Day, Postal, Enquiries, Complaints)

1. Overview

The Front Office module handles day-to-day administrative and operational activities of the school that are not directly part of teaching or curriculum but are tightly coupled with academic operations.

This module is implemented inside academic-core-service as a bounded context and may be extracted into a separate microservice in the future if required.

2. Design Principles

UI-driven domain modeling

One table per business capability

No premature optimization

Strong auditability

Academic-year aware

Single-school deployment (no multi-tenancy)

3. Common Fields (Applicable to All Front Office Tables)

All Front Office tables should include the following standard fields:

id                     (UUID / BIGINT, PK)
academic_year_id       (FK → academic_year)
status                 (ACTIVE / CLOSED / ARCHIVED)
remarks                (TEXT, optional)
created_at             (TIMESTAMP)
created_by             (USER ID)
updated_at             (TIMESTAMP)
updated_by             (USER ID)

Note: school_id is NOT included as this is a single-school deployment.
Each school deployment gets its own isolated database instance.

4. Front Office Modules & Data Models
   4.1 Visitors Log
   Purpose

Track all external visitors entering the school premises.

Table: front_office_visitors
Field Name	Description
visitor_name	Name of the visitor
phone_number	Contact number
purpose	Reason for visit
number_of_persons	Total visitors
id_proof_type	Type of ID (optional)
id_proof_number	ID number (optional)
check_in_time	Entry time
check_out_time	Exit time (nullable)
remarks	Additional notes
4.2 Phone Call Log
Purpose

Log incoming and outgoing phone calls with follow-up tracking.

Table: front_office_phone_calls
Field Name	Description
caller_name	Name of caller
phone_number	Phone number
call_date	Date of call
call_type	INCOMING / OUTGOING
call_duration	Duration
description	Call summary
next_follow_up_date	Optional follow-up
4.3 Half Day Notices
Purpose

Record early departure of students during school hours.

Table: front_office_half_day_notices
Field Name	Description
student_id	FK → student
class_id	FK → class
section_id	FK → section
out_time	Time of leaving
reason	Reason for early leave
guardian_name	Person who collected student
guardian_phone	Guardian contact number
4.4 Postal Dispatch / Receive
Purpose

Track incoming and outgoing physical mail and parcels.

Table: front_office_postal_records
Field Name	Description
direction	RECEIVED / DISPATCHED
postal_type	Letter / Parcel / Courier
reference_number	Reference ID
from_title	Sender
to_title	Receiver
courier_name	Courier service
date	Postal date
attachment_url	Scanned document
notes	Additional remarks
4.5 Admission Enquiries
Purpose

Manage prospective student admission enquiries.

Table: front_office_admission_enquiries
Field Name	Description
enquirer_name	Name of enquirer
phone_number	Contact number
enquiry_type	Parent / Student / Other
source	Website / Walk-in / Call
enquiry_date	Date of enquiry
description	Initial query
last_follow_up_date	Previous follow-up
next_follow_up_date	Planned follow-up
status	NEW / FOLLOW_UP / CONVERTED / CLOSED
4.6 Complaints
Purpose

Track and resolve complaints from parents, students, or staff.

Table: front_office_complaints
Field Name	Description
complainant_name	Name of complainant
complaint_type	Parent / Student / Staff
category	Complaint category
complaint_date	Date of complaint
description	Complaint details
action_taken	Resolution action
assigned_to_staff_id	FK → staff
internal_note	Internal comments
status	OPEN / IN_PROGRESS / RESOLVED / CLOSED
5. Relationships Summary
   Student  ──┬── Half Day Notices
   └── Admission Enquiries (indirect)

Staff    ─── Complaints (assigned_to)

Class/Section ─── Half Day Notices

6. Suggested Package Structure
   academic-core-service
   └── frontoffice
   ├── visitor
   ├── phonecall
   ├── halfday
   ├── postal
   ├── enquiry
   └── complaint


Each submodule contains:

Entity

Repository

Service

Controller

7. Future Enhancements (Not in Scope Now)

Notification triggers

SMS / Email integration

Reporting & dashboards

Workflow automation

Role-based access per module

8. Status

✅ UI-aligned
✅ Backend-ready
✅ Frontend-friendly
✅ Microservice-safe