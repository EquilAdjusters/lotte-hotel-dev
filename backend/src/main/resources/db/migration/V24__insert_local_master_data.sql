-- 로컬/데모 환경용 마스터 데이터. INSERT IGNORE로 작성되어 있어
-- 이미 같은 PK로 데이터가 존재하는 DB(예: 기존 로컬 개발 DB)에서 재실행돼도 안전하다.
-- admin1 계정은 LocalAdminInitializer(app.initial-admin.* 환경변수)가 기동 시 별도로 생성하므로 여기서 다루지 않는다.

INSERT IGNORE INTO hotel_companies
    (id, name, active, created_at, updated_at)
VALUES
    (1, '롯데호텔앤리조트', 1, '2026-08-05 17:46:52.694920', '2026-08-05 17:46:52.694920');

INSERT IGNORE INTO insurance_companies
    (id, name, active, created_at, updated_at)
VALUES
    (1, '롯데손해보험', 1, '2026-08-11 10:51:00.242611', '2026-08-11 10:51:00.242611');

INSERT IGNORE INTO hotels
    (id, name, hotel_company_id, active, created_at, updated_at)
VALUES
    (1, '롯데호텔 서울', 1, 1, '2026-08-05 17:46:55.615084', '2026-08-05 17:46:55.615084'),
    (2, '권한테스트 호텔B', 1, 1, '2026-08-10 16:46:21.489463', '2026-08-10 16:46:21.489463');

INSERT IGNORE INTO branches
    (id, hotel_id, name, active, insurance_company_id, receipt_email, created_at, updated_at)
VALUES
    (1, 1, '서울점', 1, 1, 'seoul-claim@test.com', '2026-08-05 17:47:01.433187', '2026-08-11 11:00:17.262837'),
    (2, 1, '테스트 지점 2', 1, 1, 'seoul-claim@test.com', '2026-08-10 11:20:11.768208', '2026-08-11 11:01:23.556735'),
    (3, 2, '권한테스트 지점3', 1, 1, 'seoul-claim@test.com', '2026-08-10 16:46:21.503150', '2026-08-11 11:01:28.306371');

INSERT IGNORE INTO branch_groups
    (id, name, active, created_at, updated_at)
VALUES
    (1, 'CL영업팀', 1, '2026-08-05 17:05:04.068717', '2026-08-05 17:34:36.144065'),
    (2, 'CL영업지원팀', 1, '2026-08-05 17:45:37.019292', '2026-08-05 17:45:37.019292'),
    (3, 'ADMIN4 권한테스트 그룹', 1, '2026-08-10 16:46:21.514451', '2026-08-10 16:46:21.514451');

INSERT IGNORE INTO branch_group_members
    (id, branch_group_id, branch_id, created_at, updated_at)
VALUES
    (2, 3, 1, '2026-08-10 16:46:21.525444', '2026-08-10 16:46:21.525444'),
    (3, 3, 3, '2026-08-10 16:46:21.525444', '2026-08-10 16:46:21.525444'),
    (4, 1, 1, '2026-08-10 16:50:15.010784', '2026-08-10 16:50:15.010784'),
    (5, 1, 3, '2026-08-10 16:50:15.010784', '2026-08-10 16:50:15.010784');

INSERT IGNORE INTO adjusting_companies
    (id, name, business_number, active, created_at, updated_at)
VALUES
    (1, '이퀼손해사정', '000-00-00000', 1, '2026-08-11 09:53:41.367420', '2026-08-11 10:29:08.172993'),
    (2, '이퀼손해사정', '000-00-00000', 1, '2026-08-11 09:53:43.454477', '2026-08-11 09:53:43.454477'),
    (3, '테스트손해사정', '123-45-67890', 1, '2026-08-11 10:27:12.036316', '2026-08-14 17:12:44.768853');

INSERT IGNORE INTO adjusters
    (id, adjusting_company_id, name, phone, active, created_at, updated_at)
VALUES
    (1, 1, '김철수', '02-555-2552', 1, '2026-08-11 09:53:43.464466', '2026-08-11 10:30:29.909623'),
    (2, 3, '홍길동', '010-1111-2222', 1, '2026-08-11 10:27:53.121158', '2026-08-11 10:27:53.121158');

INSERT IGNORE INTO accounts
    (id, login_id, password_hash, display_name, role, scope_type, status, shared_account,
     hotel_company_id, hotel_id, branch_id, branch_group_id,
     failed_login_count, locked_at, last_login_at, password_changed_at, deleted_at,
     created_at, updated_at)
VALUES
    (5, 'admin3-test', '{bcrypt}$2a$10$k0ROEIO6VmNNSZXbNp5cyOYnlLcP0HHO3J1Crye6.rU/gjaMmOyx.', '호텔사 관리자', 'ADMIN3', 'HOTEL', 'ACTIVE', 0,
     1, 1, NULL, NULL,
     0, NULL, '2026-08-13 09:52:19.357731', '2026-08-10 17:21:38.556469', NULL,
     '2026-08-07 10:48:01.179219', '2026-08-14 17:08:04.990015'),
    (6, 'admin4-test', '{bcrypt}$2a$10$QLXC1EACURa95yCaT7/tkuiYCkJSK.6fkc.dAMRj30B/gQY9pbOvO', '권역 관리자', 'ADMIN4', 'BRANCH_GROUP', 'ACTIVE', 1,
     NULL, NULL, NULL, 1,
     0, NULL, '2026-08-13 10:14:20.981519', '2026-08-07 10:51:35.021268', NULL,
     '2026-08-07 10:50:56.812967', '2026-08-14 17:07:10.499400'),
    (7, 'branch-test', '{bcrypt}$2a$10$Xg7DA2.w/vEt3/naT.8Xx.fZKbxAlNJqYRjUtTr2LMS/oHpD4HFn2', '서울점 공유계정', 'BRANCH_SHARED', 'BRANCH', 'ACTIVE', 1,
     1, 1, 1, NULL,
     0, NULL, '2026-08-14 16:43:44.281468', '2026-08-07 10:52:07.817266', NULL,
     '2026-08-07 10:51:06.459233', '2026-08-14 16:43:44.283285'),
    (8, 'branch_shared_test2', '{bcrypt}$2a$10$rrwUmsiuZoTL0kWK2lDQUODEYYSwyL6wPDNTLzHxpnZhrl/nhie/e', '다른지점 테스트', 'BRANCH_SHARED', 'BRANCH', 'ACTIVE', 1,
     1, 1, 2, NULL,
     0, NULL, '2026-08-10 17:04:46.930513', '2026-08-10 11:18:21.618556', NULL,
     '2026-08-10 11:18:21.619114', '2026-08-10 17:04:46.960227'),
    (9, 'branch_shared_test3', '{bcrypt}$2a$10$7ayJYLKQGqDN3g1Xwvyfee0SGjOifVzGOYLYjW/QcNScd4yX2VhF.', '권한테스트 지점3', 'BRANCH_SHARED', 'BRANCH', 'ACTIVE', 1,
     1, 2, 3, NULL,
     0, NULL, '2026-08-10 16:54:56.557369', '2026-08-10 16:52:25.583502', NULL,
     '2026-08-10 16:52:25.584074', '2026-08-10 16:54:56.558767'),
    (10, 'admin2-test', '{bcrypt}$2a$10$9GmA7/o.DZ6P/afGyfkUueRBbyhtfewQdMkd5ItMyuehqZeY47w3q', '권한테스트 지점3', 'ADMIN2', 'ALL', 'ACTIVE', 0,
     NULL, NULL, NULL, NULL,
     0, NULL, '2026-08-14 16:44:19.268390', '2026-08-11 11:15:10.533959', NULL,
     '2026-08-11 11:15:10.534511', '2026-08-14 16:44:19.270262');
