package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.PurchaseDetail;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.mapper.PhysicMapper;
import com.clwu.sms.mapper.PurchaseDetailMapper;
import com.clwu.sms.tenant.TenantContext;
import com.clwu.sms.vo.StockSummaryVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.autoconfigure.exclude="})
@Transactional
@DisplayName("PurchaseDetailServiceImpl — 进货明细集成测试（真实SQL）")
class PurchaseDetailServiceImplSqlTest {

    private static final Long TEST_TENANT_ID = 1L;

    @Autowired
    private PurchaseDetailServiceImpl purchaseDetailService;

    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;

    @Autowired
    private PhysicMapper physicMapper;

    private static final AtomicLong ID_GEN = new AtomicLong(-1000L);

    @BeforeEach
    void bindTenant() {
        TenantContext.setTenantId(TEST_TENANT_ID);
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    private static long nextId() { return ID_GEN.decrementAndGet(); }

    private void insertDetails(long batch, long physic, BigDecimal price, int status, int count) {
        List<PurchaseDetail> list = IntStream.range(0, count)
                .mapToObj(i -> PurchaseDetail.builder()
                        .batch(batch).physic(physic)
                        .buyingPrice(price).status(status)
                        .build())
                .collect(Collectors.toList());
        purchaseDetailMapper.insertBatch(list);
    }

    private void insertDetailsWithPpid(long batch, long physic, BigDecimal price,
                                        int status, long ppid, int count) {
        List<PurchaseDetail> list = IntStream.range(0, count)
                .mapToObj(i -> {
                    PurchaseDetail pd = new PurchaseDetail();
                    pd.setBatch(batch); pd.setPhysic(physic);
                    pd.setBuyingPrice(price); pd.setStatus(status);
                    pd.setPrescriptionPhysic(ppid);
                    return pd;
                })
                .collect(Collectors.toList());
        purchaseDetailMapper.insertBatch(list);
    }

    private List<PurchaseDetail> findByBatch(long batch) {
        QueryWrapper<PurchaseDetail> qw = new QueryWrapper<>();
        qw.eq("batch", batch);
        qw.orderByAsc("id");
        return purchaseDetailMapper.selectList(qw);
    }

    // ================================================================
    // addPurchaseDetail
    // ================================================================
    @Nested
    @DisplayName("addPurchaseDetail — 添加")
    class AddTest {

        @Test @DisplayName("T1: pbid=null")
        void t1() { purchaseDetailService.addPurchaseDetail(null, nextId(), bd("10"), 3); }

        @Test @DisplayName("T2: pbid=0")
        void t2() {
            long b = nextId();
            purchaseDetailService.addPurchaseDetail(0L, nextId(), bd("10"), 3);
            assertThat(findByBatch(0L)).isEmpty();
        }

        @Test @DisplayName("T3: pid=null")
        void t3() { long b = nextId(); purchaseDetailService.addPurchaseDetail(b, null, bd("10"), 3); assertThat(findByBatch(b)).isEmpty(); }

        @Test @DisplayName("T4: pid=0")
        void t4() { long b = nextId(); purchaseDetailService.addPurchaseDetail(b, 0L, bd("10"), 3); assertThat(findByBatch(b)).isEmpty(); }

        @Test @DisplayName("T5: num=0")
        void t5() { long b = nextId(); purchaseDetailService.addPurchaseDetail(b, nextId(), bd("10"), 0); assertThat(findByBatch(b)).isEmpty(); }

        @Test @DisplayName("T6: num=-1")
        void t6() { long b = nextId(); purchaseDetailService.addPurchaseDetail(b, nextId(), bd("10"), -1); assertThat(findByBatch(b)).isEmpty(); }

        @Test @DisplayName("T7: price=0")
        void t7() { long b = nextId(); purchaseDetailService.addPurchaseDetail(b, nextId(), BigDecimal.ZERO, 3); assertThat(findByBatch(b)).isEmpty(); }

        @Test @DisplayName("T8: price负数")
        void t8() { long b = nextId(); purchaseDetailService.addPurchaseDetail(b, nextId(), bd("-5"), 3); assertThat(findByBatch(b)).isEmpty(); }

        @Test @DisplayName("T9: num=3 → 3条正确")
        void t9() {
            long b = nextId(), p = nextId();
            purchaseDetailService.addPurchaseDetail(b, p, bd("12.50"), 3);
            List<PurchaseDetail> f = findByBatch(b);
            assertThat(f).hasSize(3);
            f.forEach(d -> { assertThat(d.getBatch()).isEqualTo(b); assertThat(d.getPhysic()).isEqualTo(p); assertThat(d.getBuyingPrice()).isEqualByComparingTo(bd("12.50")); assertThat(d.getStatus()).isEqualTo(StatusEnum.US_ENABLED.getCode()); });
        }
    }

    // ================================================================
    // delPurchaseDetail
    // ================================================================
    @Nested
    @DisplayName("delPurchaseDetail — 软删除")
    class DelTest {
        @Test @DisplayName("T10: 正常删除")
        void t10() {
            long b = nextId(), p = nextId();
            insertDetails(b, p, bd("10"), StatusEnum.US_ENABLED.getCode(), 3);
            purchaseDetailService.delPurchaseDetail(b, p);
            assertThat(findByBatch(b)).isEmpty();
        }
        @Test @DisplayName("T11: pbid=null")
        void t11() {
            long b = nextId(), p = nextId();
            insertDetails(b, p, bd("10"), StatusEnum.US_ENABLED.getCode(), 2);
            purchaseDetailService.delPurchaseDetail(null, p);
            findByBatch(b).forEach(d -> assertThat(d.getStatus()).isEqualTo(StatusEnum.US_ENABLED.getCode()));
        }
        @Test @DisplayName("T12: 不存在")
        void t12() { assertThat(findByBatch(nextId())).isEmpty(); }
    }

    // ================================================================
    // findByBatch
    // ================================================================
    @Nested
    @DisplayName("findByBatch — 按批次查询")
    class FindByBatchTest {
        @Test @DisplayName("T13: 3条 → 降序")
        void t13() {
            long b = nextId();
            insertDetails(b, nextId(), bd("10"), StatusEnum.US_ENABLED.getCode(), 3);
            List<PurchaseDetail> r = purchaseDetailService.findByBatch(b);
            assertThat(r).hasSize(3);
            for (int i = 0; i < r.size()-1; i++) assertThat(r.get(i).getId()).isGreaterThan(r.get(i+1).getId());
        }
        @Test @DisplayName("T14: 含DISABLE → 排除")
        void t14() {
            long b = nextId();
            insertDetails(b, nextId(), bd("10"), StatusEnum.US_ENABLED.getCode(), 2);
            insertDetails(b, nextId(), bd("10"), StatusEnum.US_DISABLE.getCode(), 1);
            assertThat(purchaseDetailService.findByBatch(b)).hasSize(2);
        }
        @Test @DisplayName("T15: 不存在")
        void t15() { assertThat(purchaseDetailService.findByBatch(-9999L)).isEmpty(); }
    }

    // ================================================================
    // findPurchaseDetailByPPid / getSizeByPPIdAndStatus
    // ================================================================
    @Nested
    @DisplayName("findByPPid / getSize")
    class ByPPidTest {
        @Test @DisplayName("T16: 2条匹配 → find返回2条")
        void t16() {
            long pid = nextId(), b = nextId();
            insertDetailsWithPpid(b, nextId(), bd("10"), StatusEnum.US_OCCUPY.getCode(), pid, 2);
            List<PurchaseDetail> r = purchaseDetailService.findPurchaseDetailByPPid(pid, StatusEnum.US_OCCUPY.getCode());
            assertThat(r).hasSize(2);
            r.forEach(d -> { assertThat(d.getPrescriptionPhysic()).isEqualTo(pid); assertThat(d.getStatus()).isEqualTo(StatusEnum.US_OCCUPY.getCode()); });
        }
        @Test @DisplayName("T17: 无匹配 → 空")
        void t17() { assertThat(purchaseDetailService.findPurchaseDetailByPPid(nextId(), StatusEnum.US_OCCUPY.getCode())).isEmpty(); }
        @Test @DisplayName("T18: getSize=3")
        void t18() {
            long pid = nextId();
            insertDetailsWithPpid(nextId(), nextId(), bd("10"), StatusEnum.US_OCCUPY.getCode(), pid, 3);
            assertThat(purchaseDetailService.getSizeByPPIdAndStatus(pid, StatusEnum.US_OCCUPY.getCode())).isEqualTo(3L);
        }
        @Test @DisplayName("T19: getSize=0")
        void t19() { assertThat(purchaseDetailService.getSizeByPPIdAndStatus(nextId(), StatusEnum.US_OCCUPY.getCode())).isZero(); }
    }

    // ================================================================
    // findListLockByPPidAndStatus
    // ================================================================
    @Nested
    @DisplayName("findListLockByPPidAndStatus — 悲观锁")
    class LockTest {
        @Test @DisplayName("T20: desc=true")
        void t20() {
            long pid = nextId();
            insertDetailsWithPpid(nextId(), nextId(), bd("10"), StatusEnum.US_OCCUPY.getCode(), pid, 3);
            List<PurchaseDetail> r = purchaseDetailService.findListLockByPPidAndStatus(pid, StatusEnum.US_OCCUPY.getCode(), true);
            assertThat(r).hasSize(3);
            for (int i = 0; i < r.size()-1; i++) assertThat(r.get(i).getId()).isGreaterThan(r.get(i+1).getId());
        }
        @Test @DisplayName("T21: desc=false")
        void t21() {
            long pid = nextId();
            insertDetailsWithPpid(nextId(), nextId(), bd("10"), StatusEnum.US_OCCUPY.getCode(), pid, 3);
            List<PurchaseDetail> r = purchaseDetailService.findListLockByPPidAndStatus(pid, StatusEnum.US_OCCUPY.getCode(), false);
            assertThat(r).hasSize(3);
            for (int i = 0; i < r.size()-1; i++) assertThat(r.get(i).getId()).isLessThan(r.get(i+1).getId());
        }
        @Test @DisplayName("T22: 无匹配")
        void t22() { assertThat(purchaseDetailService.findListLockByPPidAndStatus(nextId(), StatusEnum.US_OCCUPY.getCode(), false)).isEmpty(); }
    }

    // ================================================================
    // updPurchaseDetailOccupy
    // ================================================================
    @Nested
    @DisplayName("updPurchaseDetailOccupy — 占用")
    class OccupyTest {
        @Test @DisplayName("T23: 够 → 占用3条")
        void t23() {
            long pid = nextId(), b = nextId(), p = nextId();
            insertDetails(b, p, bd("10"), StatusEnum.US_ENABLED.getCode(), 5);
            assertThat(purchaseDetailService.updPurchaseDetailOccupy(pid, p, StatusEnum.US_ENABLED.getCode(), 3)).isEqualTo(3);
            List<PurchaseDetail> all = findByBatch(b);
            assertThat(all.stream().filter(d -> d.getStatus() == StatusEnum.US_OCCUPY.getCode()).count()).isEqualTo(3);
            assertThat(all.stream().filter(d -> d.getStatus() == StatusEnum.US_ENABLED.getCode()).count()).isEqualTo(2);
            all.stream().filter(d -> d.getStatus() == StatusEnum.US_OCCUPY.getCode()).forEach(d -> assertThat(d.getPrescriptionPhysic()).isEqualTo(pid));
        }
        @Test @DisplayName("T24: 不够 → 返回0")
        void t24() {
            long b = nextId();
            insertDetails(b, nextId(), bd("10"), StatusEnum.US_ENABLED.getCode(), 2);
            assertThat(purchaseDetailService.updPurchaseDetailOccupy(nextId(), nextId(), StatusEnum.US_ENABLED.getCode(), 3)).isZero();
        }
        @Test @DisplayName("T25: 无 → 0")
        void t25() { assertThat(purchaseDetailService.updPurchaseDetailOccupy(nextId(), nextId(), StatusEnum.US_ENABLED.getCode(), 3)).isZero(); }
    }

    // ================================================================
    // updPurchaseDetailNum
    // ================================================================
    @Nested
    @DisplayName("updPurchaseDetailNum — 状态转换")
    class UpdNumTest {
        @Test @DisplayName("T26: target=OCCUPY")
        void t26() {
            long pid = nextId(), b = nextId(), p = nextId();
            insertDetails(b, p, bd("10"), StatusEnum.US_ENABLED.getCode(), 5);
            assertThat(purchaseDetailService.updPurchaseDetailNum(pid, p, 3, StatusEnum.US_ENABLED.getCode(), StatusEnum.US_OCCUPY.getCode())).isEqualTo(3);
        }
        @Test @DisplayName("T27: target=ENABLED")
        void t27() {
            long pid = nextId(), b = nextId(), p = nextId();
            insertDetailsWithPpid(b, p, bd("10"), StatusEnum.US_OCCUPY.getCode(), pid, 5);
            assertThat(purchaseDetailService.updPurchaseDetailNum(pid, p, 3, StatusEnum.US_OCCUPY.getCode(), StatusEnum.US_ENABLED.getCode())).isEqualTo(3);
        }
        @Test @DisplayName("T28: ppid无效→0")
        void t28() {
            assertThat(purchaseDetailService.updPurchaseDetailNum(null, nextId(), 3, StatusEnum.US_ENABLED.getCode(), StatusEnum.US_OCCUPY.getCode())).isZero();
            assertThat(purchaseDetailService.updPurchaseDetailNum(0L, nextId(), 3, StatusEnum.US_ENABLED.getCode(), StatusEnum.US_OCCUPY.getCode())).isZero();
        }
        @Test @DisplayName("T29: 未知target→0")
        void t29() { assertThat(purchaseDetailService.updPurchaseDetailNum(nextId(), nextId(), 3, StatusEnum.US_ENABLED.getCode(), 999)).isZero(); }
    }

    // ================================================================
    // updPurchaseDetailEnable
    // ================================================================
    @Nested
    @DisplayName("updPurchaseDetailEnable — 释放")
    class EnableTest {
        @Test @DisplayName("T30: 释放3条")
        void t30() {
            long b = nextId(), p = nextId();
            long ppid = nextId();
            insertDetailsWithPpid(b, p, bd("10"), StatusEnum.US_OCCUPY.getCode(), ppid, 5);
            assertThat(purchaseDetailService.updPurchaseDetailEnable(
                    ppid, p, 3, StatusEnum.US_OCCUPY.getCode())).isEqualTo(3);
            assertThat(findByBatch(b).stream().filter(d -> d.getStatus() == StatusEnum.US_ENABLED.getCode()).count()).isEqualTo(3);
        }
        @Test @DisplayName("T31: 无→0")
        void t31() {
            assertThat(purchaseDetailService.updPurchaseDetailEnable(
                    nextId(), nextId(), 3, StatusEnum.US_OCCUPY.getCode())).isZero();
        }
    }

    // ================================================================
    // updPurchaseDetail
    // ================================================================
    @Nested
    @DisplayName("updPurchaseDetail — 更新")
    class UpdTest {
        @Test @DisplayName("T32: num=2 → 旧DISABLE新2条")
        void t32() {
            long b = nextId();
            long p = nextId();
            insertDetails(b, p, bd("10"), StatusEnum.US_ENABLED.getCode(), 3);
            purchaseDetailService.updPurchaseDetail(b, p, bd("15"), 2);
            List<PurchaseDetail> all = findByBatch(b);
            assertThat(all).hasSize(2);
            all.forEach(d -> assertThat(d.getStatus()).isEqualTo(StatusEnum.US_ENABLED.getCode()));
        }
        @Test @DisplayName("T33: num=0 → 全部逻辑删除")
        void t33() {
            long b = nextId();
            long p = nextId();
            insertDetails(b, p, bd("10"), StatusEnum.US_ENABLED.getCode(), 3);
            purchaseDetailService.updPurchaseDetail(b, p, bd("15"), 0);
            assertThat(findByBatch(b)).isEmpty();
        }
        @Test @DisplayName("T34: num=null → 更新price")
        void t34() {
            long b = nextId();
            long p = nextId();
            insertDetails(b, p, bd("10"), StatusEnum.US_ENABLED.getCode(), 3);
            purchaseDetailService.updPurchaseDetail(b, p, bd("20"), null);
            findByBatch(b).forEach(d -> assertThat(d.getBuyingPrice()).isEqualByComparingTo(bd("20")));
        }
        @Test @DisplayName("T35: pbid=null → return")
        void t35() {
            long b = nextId();
            insertDetails(b, nextId(), bd("10"), StatusEnum.US_ENABLED.getCode(), 2);
            purchaseDetailService.updPurchaseDetail(null, nextId(), bd("15"), 2);
            findByBatch(b).forEach(d -> assertThat(d.getStatus()).isEqualTo(StatusEnum.US_ENABLED.getCode()));
        }
    }

    // ================================================================
    // getStockSummary
    // ================================================================
    @Nested
    @DisplayName("getStockSummary — 汇总JOIN")
    class SummaryTest {
        @Test @DisplayName("T36: 有数据")
        void t36() {
            long pid = nextId();
            physicMapper.insert(Physic.builder().id(pid).name("药品A").alias("A").status(1).type(0).unit(1).manufacturer("厂").build());
            long b = nextId();
            insertDetails(b, pid, bd("10"), StatusEnum.US_ENABLED.getCode(), 3);
            insertDetails(b, pid, bd("20"), StatusEnum.US_OCCUPY.getCode(), 2);
            StockSummaryVo vo = purchaseDetailService.getStockSummary().stream().filter(s -> s.getPhysicId().equals(pid)).findFirst().orElse(null);
            assertThat(vo).isNotNull();
            assertThat(vo.getPhysicName()).isEqualTo("药品A");
            assertThat(vo.getAvailableQty()).isEqualTo(3);
            assertThat(vo.getOccupiedQty()).isEqualTo(2);
            assertThat(vo.getAvgBuyingPrice()).isEqualByComparingTo(bd("10"));
        }
        @Test @DisplayName("T37: 无明细")
        void t37() {
            long pid = nextId();
            physicMapper.insert(Physic.builder().id(pid).name("空库存").alias("空").status(1).type(0).unit(1).manufacturer("厂").build());
            StockSummaryVo vo = purchaseDetailService.getStockSummary().stream().filter(s -> s.getPhysicId().equals(pid)).findFirst().orElse(null);
            assertThat(vo).isNotNull();
            assertThat(vo.getAvailableQty()).isZero();
            assertThat(vo.getOccupiedQty()).isZero();
            assertThat(vo.getAvgBuyingPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // ================================================================
    // findPurchaseDetail
    // ================================================================
    @Nested
    @DisplayName("findPurchaseDetail — 空方法")
    class FindTest {
        @Test @DisplayName("T38: 不抛异常")
        void t38() { purchaseDetailService.findPurchaseDetail(1L, 1L, 0, LocalDateTime.now(), LocalDateTime.now()); }
    }

    private static BigDecimal bd(String v) { return new BigDecimal(v); }
}
