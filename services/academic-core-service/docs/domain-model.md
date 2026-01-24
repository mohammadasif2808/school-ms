Status: FROZEN – Domain v1.0

# DOMAIN MODEL: ACADEMIC CORE SERVICE

## 1. Domain Concept Overview
This service is the "Source of Truth" for the institutional hierarchy and the people within it. It relies on a "Temporal Hierarchical Model" where structural definitions (Classes, Sections) are combined with Time (Academic Year) to create active containers (ClassSections) for Students (Enrollments).

---

## 2. Core Domain Entities

### A. Academic Structure (Definitions)

#### 1. AcademicYear
*Represents the temporal boundary of all academic operations.*
- **Responsibility:** Defines the period during which Enrollments and ClassSections are valid.
- **Attributes:** `id`, `name` (e.g., "2025-2026"), `startDate`, `endDate`, `isCurrent` (boolean flag).

#### 2. Class (GradeLevel)
*Represents a pedagogical level (e.g., Grade 1, Grade 10).*
- **Responsibility:** Groups students by academic progression level.
- **Attributes:** `id`, `name`, `levelOrder` (integer for sorting/logic), `description`.

#### 3. Section
*Represents a division identifier (e.g., A, B, Blue).*
- **Responsibility:** Distinct reusable labels for subdividing Classes.
- **Attributes:** `id`, `name` (e.g., "A", "Rose").

#### 4. Subject
*Represents a topic of study.*
- **Responsibility:** Syllabus definition.
- **Attributes:** `id`, `name`, `subjectCode`, `isOptional` (boolean), `type` (Theory/Practical).

---

### B. People (Institutional Profiles)

#### 5. Student
*The cornerstone entity. Represents the individual, independent of time.*
- **Responsibility:** Holds static archival data of the learner.
- **Attributes:** `id`, `admissionNumber` (Global Unique ID), `firstName`, `lastName`, `dob`, `gender`, `joiningDate`, `status` (Active, Alumni, Withdrawn).
- **Identity Ref:** `userId` (Optional/Nullable).

#### 6. Staff
*Represents employees/contractors (Teachers, Admins, Drivers).*
- **Responsibility:** Resource management for academic delivery.
- **Attributes:** `id`, `employeeId`, `firstName`, `lastName`, `designation`, `qualification`, `mobile`, `email`.
- **Identity Ref:** `userId` (Optional/Nullable).

#### 7. Parent (Guardian)
*Represents legal guardians.*
- **Responsibility:** POC for communication and responsibility.
- **Attributes:** `id`, `firstName`, `lastName`, `relationship` (Mother/Father), `mobile`, `email`, `address`.
- **Identity Ref:** `userId` (Optional/Nullable).

---

### C. Physical Infrastructure

#### 8. Classroom
*Represents a physical brick-and-mortar room.*
- **Responsibility:** Resource allocation and capacity planning.
- **Attributes:** `id`, `roomNumber`, `capacity`, `infraType` (Lab, Lecture Hall), `buildingBlock`.

---

### D. Core Academic Glue (Temporal Associations)

#### 9. ClassSection (The "Active Class")
*The intersection of Class, Section, and AcademicYear.*
- **Responsibility:** Container for students and assignments for a specific year.
- **Attributes:** `id`, `medium` (English/French/etc.).
- **Relations:** 
  - `classId` (1:1)
  - `sectionId` (1:1)
  - `academicYearId` (1:1)
  - `classTeacherId` (0:1 Staff reference)
  - `classroomId` (0:1 Physical room reference)
    - *Note:* Classroom assignment is **OPTIONAL** and **CHANGEABLE** during the year.
    - *Constraint:* Changing the room does NOT affect Enrollment, Roll Numbers, or Academic History.

#### 10. Enrollment (The "Active Student")
*The specific placement of a Student in a ClassSection.*
- **Responsibility:** Tracks academic journey history.
- **Attributes:** `id`, `rollNumber` (Context-specific ID).
- **Relations:**
  - `studentId` (1:1)
  - `classSectionId` (1:1)
  - `status` (Active, Promoted, Detained)
  - `enrollmentDate`

#### 11. SubjectAssignment (The "Curriculum")
*Defines what a Class studies in a specific Year.*
- **Responsibility:** Syllabus mapping.
- **Relations:**
  - `classId` (1:1)
  - `subjectId` (1:1)
  - `academicYearId` (1:1)
  - `sectionId` (0:1 Optional) - *Future Flexibility*
    - *Constraint:* If `sectionId` is null, subject applies to ALL sections of the class.
    - *Constraint:* If `sectionId` is present, subject is limited ONLY to that section.

#### 12. StaffAssignment (The "Workload")
*Defines who teaches what, where, and when.*
- **Responsibility:** Timetable basis. A Staff member can have **MULTIPLE** assignments.
- **Definition:** One assignment represents **One Subject** taught to **One ClassSection** in **One AcademicYear**.
- **Relations:**
  - `staffId` (1:1)
  - `subjectId` (1:1)
  - `classSectionId` (1:1) (Implicitly includes AcademicYear)

#### 13. StudentGuardian
*Link table for N:M relationship between Student and Parent.*
- **Responsibility:** Family mapping.
- **Relations:**
  - `studentId`
  - `parentId`
  - `isPrimaryContact` (boolean)

---

## 3. Relationships & Cardinality

| Source Entity | Relationship | Target Entity | Cardinality | Interpretation |
|:---|:---|:---|:---|:---|
| **ClassSection** | *belongs_to* | **Class** | N:1 | Many yearly sections (5-A, 5-B) belong to "Grade 5". |
| **ClassSection** | *belongs_to* | **AcademicYear** | N:1 | Context valid only for that year. |
| **ClassSection** | *has_teacher* | **Staff** | N:1 | One class teacher per section per year. |
| **Enrollment** | *belongs_to* | **Student** | N:1 | A student has many enrollments over time (history). |
| **Enrollment** | *belongs_to* | **ClassSection** | N:1 | A section has many students enrolled. |
| **StaffAssignment** | *teaches* | **ClassSection** | N:1 | A teacher teaches a specific section. |
| **StaffAssignment** | *covers* | **Subject** | N:1 | Teaching a specific subject. |
| **Student** | *has* | **Parent** | N:M | via StudentGuardian link. |

---

## 4. Lifecycle Rules

### Enrollment Lifecycle
1.  **Draft/Admitted:** Student is added to system.
2.  **Enrolled:** Student assigned to `ClassSection` for `AcademicYear` (Enrollment created).
3.  **Active:** Student attends classes.
4.  **Promoted:** End of year, Student moves to next Class (New Enrollment created for next Year; Old Enrollment marked 'Promoted').
5.  **Detained:** Student stays in same Class for next Year (New Enrollment created for same Class; Old Enrollment marked 'Detained').
6.  **Withdrawn:** Student leaves school (Enrollment marked 'Withdrawn', Student marked 'Alumni' or 'Inactive').

### ClassSection Lifecycle
1.  **Creation:** Created at start of `AcademicYear`.
2.  **Initialization:** Class Teacher assigned, Room assigned.
3.  **Active:** Enrollments added.
4.  **Archived:** Year ends. `ClassSection` becomes read-only history.

---

## 5. Mandatory vs Optional

### Mandatory
- **Enrollment:** `rollNumber` (Must be unique within ClassSection), `academicYear`.
- **Student:** `admissionNumber` (Global Unique).
- **Staff:** `employeeId`.
- **ClassSection:** `class`, `section`, `academicYear`.

### Optional
- **Profile:** `userId` (Person might not have login access yet).
- **ClassSection:** `classTeacher` (Can be assigned later), `classroom`.
- **Student:** `bloodGroup`, `address` (Can be updated later).

---

## 6. Constraints & Invariants

1.  **Temporal Constraint:** A Student cannot have **two active Enrollments** in overlapping AcademicYears.
2.  **Unique Roll Number:** `(classSectionId, rollNumber)` must be unique.
3.  **Unique Admission:** `admissionNumber` is unique system-wide.
4.  **Curriculum Consistency:** A `StaffAssignment` cannot assign a Subject to a ClassSection if that Subject is not mapped to the Class in `SubjectAssignment` for that Year (i.e., You can't teach "Rocket Science" to Grade 1 if Grade 1 curriculum doesn't include it).
5.  **Identity Boundary:** This service manages the *profile*, not the *credential*. Deleting a Student profile here does NOT delete the Auth User, but logically orphans it.

## 7. Identity Service Integration

The following entities reference the immutable `userId` (GUID) from `identity-service`:

1.  **Student:** `userId` (Nullable)
2.  **Staff:** `userId` (Nullable)
3.  **Parent:** `userId` (Nullable)

*Note: This service treats `userId` as a dumb string property for correlation.*
