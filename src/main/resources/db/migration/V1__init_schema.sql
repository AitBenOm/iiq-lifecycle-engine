-- V1__init_schema.sql
-- Schéma minimal IIQ-like : cases, work items, provisioning plans, audit logs

CREATE TABLE IF NOT EXISTS lifecycle_cases (
                                               id              UUID PRIMARY KEY,
                                               identity_id      VARCHAR(100) NOT NULL,
    event_type       VARCHAR(20)  NOT NULL, -- JOINER / MOVER / LEAVER
    status           VARCHAR(30)  NOT NULL, -- DRAFT / WAITING_APPROVAL / APPROVED / REJECTED / PROVISIONED ...
    attributes       JSONB        NOT NULL DEFAULT '{}'::jsonb,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_cases_identity_id ON lifecycle_cases(identity_id);
CREATE INDEX IF NOT EXISTS idx_cases_status     ON lifecycle_cases(status);

CREATE TABLE IF NOT EXISTS work_items (
                                          id              UUID PRIMARY KEY,
                                          case_id          UUID        NOT NULL REFERENCES lifecycle_cases(id) ON DELETE CASCADE,
    name            VARCHAR(120) NOT NULL,
    status          VARCHAR(30)  NOT NULL, -- PENDING / APPROVED / REJECTED
    assignee        VARCHAR(120),
    decision        VARCHAR(20),           -- APPROVE / REJECT
    comment         TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    decided_at      TIMESTAMPTZ
    );

CREATE INDEX IF NOT EXISTS idx_work_items_case_id ON work_items(case_id);
CREATE INDEX IF NOT EXISTS idx_work_items_status  ON work_items(status);

CREATE TABLE IF NOT EXISTS provisioning_plans (
                                                  id              UUID PRIMARY KEY,
                                                  case_id          UUID        NOT NULL UNIQUE REFERENCES lifecycle_cases(id) ON DELETE CASCADE,
    status          VARCHAR(30)  NOT NULL, -- READY / EXECUTING / DONE / FAILED
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_plans_status ON provisioning_plans(status);

CREATE TABLE IF NOT EXISTS provisioning_plan_steps (
                                                       id              UUID PRIMARY KEY,
                                                       plan_id          UUID        NOT NULL REFERENCES provisioning_plans(id) ON DELETE CASCADE,
    step_order      INTEGER     NOT NULL,
    target_system   VARCHAR(80) NOT NULL,   -- ex: "AD", "SAP", "LDAP" (fake au début)
    action_type     VARCHAR(30) NOT NULL,   -- CREATE_ACCOUNT / UPDATE_ATTR / DISABLE_ACCOUNT ...
    payload         JSONB       NOT NULL DEFAULT '{}'::jsonb,
    status          VARCHAR(30) NOT NULL,   -- PENDING / DONE / FAILED
    error_message   TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_steps_plan_id ON provisioning_plan_steps(plan_id);

CREATE TABLE IF NOT EXISTS audit_logs (
                                          id              UUID PRIMARY KEY,
                                          case_id          UUID REFERENCES lifecycle_cases(id) ON DELETE SET NULL,
    event_type       VARCHAR(80)  NOT NULL, -- ex: CASE_CREATED, WORKITEM_DECIDED, PLAN_EXECUTED...
    details          JSONB        NOT NULL DEFAULT '{}'::jsonb,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_audit_case_id ON audit_logs(case_id);
CREATE INDEX IF NOT EXISTS idx_audit_event   ON audit_logs(event_type);
