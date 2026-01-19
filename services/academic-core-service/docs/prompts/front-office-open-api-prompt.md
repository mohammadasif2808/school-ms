AGENTIC PROMPT: Front Office → OpenAPI Contract Generation
🎯 Objective

You are an expert backend architect and API designer.
Your task is to analyze UI screenshots + documented data models and generate a complete, production-ready OpenAPI 3.0 specification for the Front Office module inside the academic-core-service.

📂 Context & Inputs

You have access to the following authoritative sources:

1️⃣ UI Screenshots (Primary Source of Truth)

All screenshots for Front Office are stored at:

school-ms/services/academic-core-service/screen-views/front-office/


Screens include:

Visitors Log (list + add)

Phone Call Log (list + add)

Half Day Notices (list + add)

Postal Dispatch / Receive (list + add)

Admission Enquiries (list + add)

Complaints (list + add)

👉 Treat UI fields, filters, tables, and actions as mandatory API requirements.

2️⃣ Domain Documentation

Data model documentation is available at:

school-ms/services/academic-core-service/docs/front-office-data-model.md


This document defines:

Entities

Fields

Relationships

Status enums

Audit fields

👉 The OpenAPI schema must not contradict this document.

🧠 Responsibilities (VERY IMPORTANT)

You MUST:

Inspect every UI screen

Identify list views, create/update forms, filters, and actions

Derive API endpoints from user actions

Map UI → Data Model → API

Every UI field must exist in request/response schemas

Every table column must be retrievable via APIs

Design RESTful APIs

Follow REST naming conventions

Use plural resource names

Use proper HTTP verbs

Support List Operations
Each list endpoint discoverable from UI must support:

Pagination

Sorting

Filtering (as shown in UI)

Search (name / phone / ref no etc.)

Model Academic Context
Every request must implicitly or explicitly support:

schoolId

academicYearId

📦 Modules to Cover

Generate OpenAPI paths & schemas for:

1. Visitors Log

List visitors

Add visitor

Update checkout time

Filter by purpose / date

2. Phone Call Log

List calls

Log call

Filter by date, call type

Follow-up date support

3. Half Day Notices

List notices

Create notice

Filter by class / date

4. Postal Dispatch / Receive

List postal records

Add postal record

Filter by direction, date

5. Admission Enquiries

List enquiries

Create enquiry

Update status

Follow-up tracking

6. Complaints

List complaints

File complaint

Assign staff

Update status / action taken

🧾 OpenAPI Specification Rules

You MUST produce:

OpenAPI version: 3.0.3

YAML format

Single consolidated file

File path:

school-ms/services/academic-core-service/docs/openapi-front-office.yaml

📑 Required Sections

Your OpenAPI spec MUST include:

Info

Title: Academic Core Service – Front Office API

Version: v1

Tags

One tag per module (visitors, calls, enquiries, etc.)

Paths

Clearly defined endpoints

Summary & description for each

Schemas

Request DTOs

Response DTOs

Enum definitions

Pagination response model

Error Responses

400, 401, 404, 409, 500

🚦 Validation & Quality Gate

Before final output, you MUST:

Cross-verify all UI fields exist in schemas

Ensure no missing list or create endpoints

Ensure naming consistency with data model

Ensure enums are centralized

Ensure request/response symmetry

❌ Do NOT:

Invent UI fields not present

Merge unrelated modules

Over-optimize or micro-split endpoints

✅ Final Output Expectation

Produce:

A complete OpenAPI YAML

Fully aligned with:

UI screenshots

Front Office data model

Ready for:

Frontend integration

Controller generation

Contract-first development

🧠 Operating Mode

Work step-by-step internally, but output only the final OpenAPI YAML.

You are allowed to take time, but do not skip anything.