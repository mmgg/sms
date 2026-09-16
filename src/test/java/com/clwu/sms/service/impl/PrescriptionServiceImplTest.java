package com.clwu.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.clwu.sms.entity.Physic;
import com.clwu.sms.entity.Prescription;
import com.clwu.sms.entity.PrescriptionPhysic;
import com.clwu.sms.entity.SellingPrice;
import com.clwu.sms.enums.StatusEnum;
import com.clwu.sms.exception.BusinessException;
import com.clwu.sms.mapper.PrescriptionMapper;
import com.clwu.sms.mapper.PurchaseDetailMapper;
import com.clwu.sms.service.*;
import com.clwu.sms.vo.PrescriptionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrescriptionServiceImpl — 处方管理服务单元测试")
class PrescriptionServiceImplTest {

    @Mock
    private PrescriptionMapper perscriptionMapper;

    @Mock
    private PurchaseDetailMapper parchaseDetailMapper;

    @Mock
    private PrescriptionPhysicService prescriptionPhysicService;

    @Mock
    private SellingPricingService sellingPricingService;

    @Mock
    private PhysicService physicService;

    @Mock
    private PurchaseDetailService purchaseDetailService;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    @Captor
    private ArgumentCaptor<Prescription> prescriptionCaptor;

    @Captor
    private ArgumentCaptor<UpdateWrapper<Prescription>> updateWrapperCaptor;

    // ================================================================
    // 方法 1: addPrescription
    // ================================================================
    @Nested
    @DisplayName("addPrescription — 添加处方")
    class AddPrescriptionTest {

        @Test
        @DisplayName("T1: 合法的 Prescription → 调用 mapper.insert()")
        void givenValidPrescription_whenAdd_thenInsert() {
            Prescription prescription = Prescription.builder()
                    .patient(1L).user(1L).comments("测试").build();

            prescriptionService.addPrescription(prescription);

            verify(perscriptionMapper).insert(prescription);
        }

        @Test
        @DisplayName("T2: ID 为负值 → 仍然调用 mapper.insert()")
        void givenNegativeId_whenAdd_thenInsert() {
            Prescription prescription = Prescription.builder()
                    .id(-1L).patient(1L).user(1L).build();

            prescriptionService.addPrescription(prescription);

            verify(perscriptionMapper).insert(prescription);
        }

        @Test
        @DisplayName("T3: null → 抛出异常")
        void givenNull_whenAdd_thenThrows() {
            assertThatThrownBy(() -> prescriptionService.addPrescription(null))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ================================================================
    // 方法 2: delPrescription
    // ================================================================
    @Nested
    @DisplayName("delPrescription — 删除处方（软删除）")
    class DelPrescriptionTest {

        @Test
        @DisplayName("T4: id 存在 → 释放库存并逻辑删除")
        void givenExistingId_whenDelete_thenSoftDelete() {
            Long id = 1L;
            Prescription prescription = Prescription.builder().id(id).build();
            when(perscriptionMapper.selectById(id)).thenReturn(prescription);
            when(prescriptionPhysicService.findPrescriptionPhysic(
                    id, StatusEnum.US_ENABLED.getCode())).thenReturn(Collections.emptyList());

            prescriptionService.delPrescription(id);

            verify(perscriptionMapper).deleteById(id);
        }

        @Test
        @DisplayName("T5: id = null → 不执行任何操作")
        void givenNullId_whenDelete_thenDoNothing() {
            prescriptionService.delPrescription(null);

            verify(perscriptionMapper, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("T6: id 不存在 → 不执行删除")
        void givenNonExistingId_whenDelete_thenAffectsZeroRows() {
            prescriptionService.delPrescription(9999L);

            verify(perscriptionMapper, never()).deleteById(anyLong());
        }
    }

    // ================================================================
    // 方法 3: updPrescription
    // ================================================================
    @Nested
    @DisplayName("updPrescription — 更新处方")
    class UpdPrescriptionTest {

        @Test
        @DisplayName("T7: id > 0 → 调用 mapper.updateById()")
        void givenValidPrescription_whenUpdate_thenUpdateById() {
            Prescription prescription = Prescription.builder()
                    .id(1L).comments("更新备注").build();

            prescriptionService.updPrescription(prescription);

            verify(perscriptionMapper).updateById(any());
        }

        @Test
        @DisplayName("T8: id = null → 不执行更新")
        void givenNullId_whenUpdate_thenDoNothing() {
            Prescription prescription = Prescription.builder()
                    .comments("更新备注").build();

            prescriptionService.updPrescription(prescription);

            verify(perscriptionMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("T9: id = 0 → 不执行更新")
        void givenZeroId_whenUpdate_thenDoNothing() {
            Prescription prescription = Prescription.builder()
                    .id(0L).comments("更新备注").build();

            prescriptionService.updPrescription(prescription);

            verify(perscriptionMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("T10: id = -1 → 不执行更新（-1 > 0 为 false）")
        void givenNegativeId_whenUpdate_thenDoNothing() {
            Prescription prescription = Prescription.builder()
                    .id(-1L).comments("更新备注").build();

            prescriptionService.updPrescription(prescription);

            verify(perscriptionMapper, never()).updateById(any());
        }
    }

    // ================================================================
    // 方法 4: findPrescriptionById(Long)
    // ================================================================
    @Nested
    @DisplayName("findPrescriptionById(Long) — 按 ID 查询")
    class FindPrescriptionByIdTest {

        @Test
        @DisplayName("T11: id 存在 → 返回 Prescription 对象")
        void givenExistingId_whenFindById_thenReturnPrescription() {
            Long id = 1L;
            Prescription expected = Prescription.builder().id(1L).patient(1L).build();
            when(perscriptionMapper.selectById(id)).thenReturn(expected);

            Prescription result = prescriptionService.findPrescriptionById(id);

            assertThat(result).isEqualTo(expected);
            verify(perscriptionMapper).selectById(id);
        }

        @Test
        @DisplayName("T12: id = null → 返回 null")
        void givenNullId_whenFindById_thenReturnNull() {
            Prescription result = prescriptionService.findPrescriptionById(null);

            assertThat(result).isNull();
            verify(perscriptionMapper, never()).selectById(any());
        }

        @Test
        @DisplayName("T13: id = 0 → 返回 null")
        void givenZeroId_whenFindById_thenReturnNull() {
            Prescription result = prescriptionService.findPrescriptionById(0L);

            assertThat(result).isNull();
            verify(perscriptionMapper, never()).selectById(any());
        }

        @Test
        @DisplayName("T14: id 不存在 → 返回 null")
        void givenNonExistingId_whenFindById_thenReturnNull() {
            when(perscriptionMapper.selectById(9999L)).thenReturn(null);

            Prescription result = prescriptionService.findPrescriptionById(9999L);

            assertThat(result).isNull();
        }
    }

    // ================================================================
    // 方法 5: findPrescriptionById(Long pid, int status, LocalDateTime ...)
    // ================================================================
    @Nested
    @DisplayName("findPrescriptionById(Long, int, LocalDateTime, LocalDateTime) — 按条件查询")
    class FindPrescriptionByConditionTest {

        @Test
        @DisplayName("T15: 合法参数 → 返回处方列表")
        void givenValidParams_whenFind_thenReturnList() {
            Long pid = 1L;
            LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);
            Prescription p = Prescription.builder().id(1L).patient(1L).build();
            when(perscriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(p));

            List<Prescription> result = prescriptionService.findPrescriptionById(pid, 0, start, end);

            assertThat(result).hasSize(1).contains(p);
            verify(perscriptionMapper).selectList(any(QueryWrapper.class));
        }

        @Test
        @DisplayName("T16: 时间范围内无数据 → 返回空列表")
        void givenNoDataInRange_whenFind_thenReturnEmptyList() {
            when(perscriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

            List<Prescription> result = prescriptionService.findPrescriptionById(
                    1L, 0,
                    LocalDateTime.of(2020, 1, 1, 0, 0),
                    LocalDateTime.of(2020, 12, 31, 23, 59));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("T17: pid 不存在 → 返回空列表")
        void givenNonExistingPid_whenFind_thenReturnEmptyList() {
            when(perscriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

            List<Prescription> result = prescriptionService.findPrescriptionById(
                    9999L, 0,
                    LocalDateTime.of(2020, 1, 1, 0, 0),
                    LocalDateTime.of(2020, 12, 31, 23, 59));

            assertThat(result).isEmpty();
        }
    }

    // ================================================================
    // 方法 6: createPrescriptionWithItems
    // ================================================================
    @Nested
    @DisplayName("createPrescriptionWithItems — 创建处方并添加药品（事务型）")
    class CreatePrescriptionWithItemsTest {

        private PrescriptionRequest makeRequest(Long patient, Long user, String comments,
                                                  Long physic, Integer num) {
            PrescriptionRequest req = new PrescriptionRequest();
            req.setPatient(patient);
            req.setUser(user);
            req.setComments(comments);
            PrescriptionRequest.Item item = new PrescriptionRequest.Item();
            item.setPhysic(physic);
            item.setNum(num);
            item.setSelling(null);
            req.setItems(Collections.singletonList(item));
            return req;
        }

        /** 模拟 MyBatis Plus insert 自增 ID 回写 */
        private void mockInsertAutoGenId() {
            doAnswer(invocation -> {
                Prescription arg = invocation.getArgument(0);
                arg.setId(100L);
                return 1;
            }).when(perscriptionMapper).insert(any(Prescription.class));
        }

        /** 模拟库存充足（selectCountForLock 返回值 ≥ 需求数量） */
        private void mockStockAvailable(Long physic, int available) {
            when(parchaseDetailMapper.selectCountForLock(physic, StatusEnum.US_ENABLED.getCode()))
                    .thenReturn(available);
        }

        /** 模拟售价配置正常（恰好 1 条） */
        private void mockSellingPriceOk(Long physic) {
            SellingPrice sp = new SellingPrice();
            sp.setId(10L);
            sp.setPrice(new BigDecimal("15.00"));
            when(sellingPricingService.findSellingPriceByPhysic(physic, StatusEnum.US_ENABLED.getCode()))
                    .thenReturn(Collections.singletonList(sp));
        }

        private void mockPhysicName(Long physic, String name) {
            Physic p = Physic.builder().id(physic).name(name).build();
            when(physicService.findPhysicById(physic)).thenReturn(p);
        }

        // ---- Happy Path ----

        @Test
        @DisplayName("T18: 1 个药品，库存充足，售价正确 → 成功创建处方，返回 rxId")
        void givenSingleItemWithStock_whenCreate_thenSuccess() {
            mockInsertAutoGenId();
            mockStockAvailable(1L, 10);
            mockSellingPriceOk(1L);
            //mockOccupySuccess(1L, 5);
            // createPrescriptionWithItems 内部会逐个调用下面两个方法
            doNothing().when(prescriptionPhysicService).addPrescriptionPhysic(any());
            doNothing().when(prescriptionPhysicService).updConstAndIncomeById(any());

            when(purchaseDetailService.updPurchaseDetailNum(
                    any(), eq(1L), eq(5),
                    eq(StatusEnum.US_ENABLED.getCode()),
                    eq(StatusEnum.US_OCCUPY.getCode())))
                    .thenReturn(10);

            PrescriptionRequest request = makeRequest(1L, 1L, "测试", 1L, 5);
            Long rxId = prescriptionService.createPrescriptionWithItems(request);

            assertThat(rxId).isEqualTo(100L);
            verify(perscriptionMapper).insert(any(Prescription.class));
            verify(prescriptionPhysicService).addPrescriptionPhysic(any());
            verify(purchaseDetailService).updPurchaseDetailNum(any(), eq(1L), eq(5), eq(1), eq(2));
            verify(prescriptionPhysicService).updConstAndIncomeById(any());
        }

        @Test
        @DisplayName("T19: 多个药品，全部库存充足 → 逐个处理全部成功")
        void givenMultiItemsWithStock_whenCreate_thenAllProcessed() {
            mockInsertAutoGenId();
            // 两个药品库存都充足
            mockStockAvailable(1L, 10);
            mockStockAvailable(2L, 20);
            mockSellingPriceOk(1L);
            mockSellingPriceOk(2L);
            // 两个药品的 occupy 分别 mock（ppId 在代码中按插入顺序为 100L）
            when(purchaseDetailService.updPurchaseDetailNum(
                    any(), eq(1L), eq(3),
                    eq(StatusEnum.US_ENABLED.getCode()),
                    eq(StatusEnum.US_OCCUPY.getCode())))
                    .thenReturn(1);
            when(purchaseDetailService.updPurchaseDetailNum(
                    any(), eq(2L), eq(5),
                    eq(StatusEnum.US_ENABLED.getCode()),
                    eq(StatusEnum.US_OCCUPY.getCode())))
                    .thenReturn(1);
            doNothing().when(prescriptionPhysicService).addPrescriptionPhysic(any());
            doNothing().when(prescriptionPhysicService).updConstAndIncomeById(any());

            PrescriptionRequest req = new PrescriptionRequest();
            req.setPatient(1L);
            req.setUser(1L);
            req.setComments("多药品");
            PrescriptionRequest.Item item1 = new PrescriptionRequest.Item();
            item1.setPhysic(1L);
            item1.setNum(3);
            PrescriptionRequest.Item item2 = new PrescriptionRequest.Item();
            item2.setPhysic(2L);
            item2.setNum(5);
            req.setItems(Arrays.asList(item1, item2));

            Long rxId = prescriptionService.createPrescriptionWithItems(req);

            assertThat(rxId).isEqualTo(100L);
            verify(prescriptionPhysicService, times(2)).addPrescriptionPhysic(any());
            verify(purchaseDetailService, times(2)).updPurchaseDetailNum(
                    any(), anyLong(), anyInt(), anyInt(), anyInt());
            verify(prescriptionPhysicService, times(2)).updConstAndIncomeById(any());
        }

        // ---- 异常路径 ----

        @Test
        @DisplayName("T20: 库存不足 → 抛出 BusinessException(400)")
        void givenInsufficientStock_whenCreate_thenThrowBusinessException() {
            mockInsertAutoGenId();
            mockStockAvailable(1L, 2);  // 需要 5，可用 2
            mockPhysicName(1L, "阿莫西林");

            PrescriptionRequest request = makeRequest(1L, 1L, "测试", 1L, 5);

            assertThatThrownBy(() -> prescriptionService.createPrescriptionWithItems(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("库存不足")
                    .hasMessageContaining("阿莫西林")
                    .hasMessageContaining("需5")
                    .hasMessageContaining("可用2");
        }

        @Test
        @DisplayName("T21: 售价配置异常（0 条售价记录）→ 抛出 BusinessException(400)")
        void givenNoSellingPrice_whenCreate_thenThrowBusinessException() {
            mockInsertAutoGenId();
            mockStockAvailable(1L, 10);
            when(sellingPricingService.findSellingPriceByPhysic(1L, StatusEnum.US_ENABLED.getCode()))
                    .thenReturn(Collections.emptyList());

            PrescriptionRequest request = makeRequest(1L, 1L, "测试", 1L, 5);

            assertThatThrownBy(() -> prescriptionService.createPrescriptionWithItems(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("售价配置有问题");
        }

        @Test
        @DisplayName("T21b: 售价配置异常（2 条售价记录）→ 抛出 BusinessException(400)")
        void givenMultipleSellingPrices_whenCreate_thenThrowBusinessException() {
            mockInsertAutoGenId();
            mockStockAvailable(1L, 10);
            SellingPrice sp1 = new SellingPrice(); sp1.setId(10L);
            SellingPrice sp2 = new SellingPrice(); sp2.setId(20L);
            when(sellingPricingService.findSellingPriceByPhysic(1L, StatusEnum.US_ENABLED.getCode()))
                    .thenReturn(Arrays.asList(sp1, sp2));

            PrescriptionRequest request = makeRequest(1L, 1L, "测试", 1L, 5);

            assertThatThrownBy(() -> prescriptionService.createPrescriptionWithItems(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("售价配置有问题");
        }

        @Test
        @DisplayName("T22: 占用库存失败 → 抛出 BusinessException(500)")
        void givenOccupyFails_whenCreate_thenThrowBusinessException() {
            mockInsertAutoGenId();
            mockStockAvailable(1L, 10);
            mockSellingPriceOk(1L);
            mockPhysicName(1L, "阿莫西林");
            // occupy 返回 0（失败）
            doNothing().when(prescriptionPhysicService).addPrescriptionPhysic(any());
            when(purchaseDetailService.updPurchaseDetailNum(
                    any(), eq(1L), eq(5),
                    eq(StatusEnum.US_ENABLED.getCode()),
                    eq(StatusEnum.US_OCCUPY.getCode())))
                    .thenReturn(0);

            PrescriptionRequest request = makeRequest(1L, 1L, "测试", 1L, 5);

            assertThatThrownBy(() -> prescriptionService.createPrescriptionWithItems(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("占用库存失败")
                    .hasMessageContaining("阿莫西林");
        }

        // ---- 边界条件 ----

        @Test
        @DisplayName("T23: comments = null → 默认设为空字符串")
        void givenNullComments_whenCreate_thenDefaultToEmpty() {
            mockInsertAutoGenId();
            mockStockAvailable(1L, 10);
            mockSellingPriceOk(1L);
            //mockOccupySuccess(1L, 5);
            doNothing().when(prescriptionPhysicService).addPrescriptionPhysic(any());
            doNothing().when(prescriptionPhysicService).updConstAndIncomeById(any());

            when(purchaseDetailService.updPurchaseDetailNum(
                    any(), eq(1L), eq(5),
                    eq(StatusEnum.US_ENABLED.getCode()),
                    eq(StatusEnum.US_OCCUPY.getCode())))
                    .thenReturn(1);

            // comments 为 null
            PrescriptionRequest request = makeRequest(1L, 1L, null, 1L, 5);
            Long rxId = prescriptionService.createPrescriptionWithItems(request);

            assertThat(rxId).isEqualTo(100L);
        }

        @Test
        @DisplayName("T24: items 为空 → 抛出 BusinessException")
        void givenEmptyItems_whenCreate_thenThrowBusinessException() {
            mockInsertAutoGenId();

            PrescriptionRequest req = new PrescriptionRequest();
            req.setPatient(1L);
            req.setUser(1L);
            req.setComments("空处方");
            req.setItems(Collections.emptyList());

            assertThatThrownBy(() -> prescriptionService.createPrescriptionWithItems(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("至少需要一种");
            verify(prescriptionPhysicService, never()).addPrescriptionPhysic(any());
        }

        @Test
        @DisplayName("T25: request = null → 抛出 BusinessException")
        void givenNullRequest_whenCreate_thenThrowsBusinessException() {
            assertThatThrownBy(() -> prescriptionService.createPrescriptionWithItems(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("不能为空");
        }

        @Test
        @DisplayName("T26: 多药品中第二个药品库存不足 → 抛出 BusinessException")
        void givenSecondItemInsufficientStock_whenCreate_thenThrowBusinessException() {
            mockInsertAutoGenId();
            // 第一个药品库存充足
            mockStockAvailable(1L, 10);
            mockSellingPriceOk(1L);
            doNothing().when(prescriptionPhysicService).addPrescriptionPhysic(any());
            when(purchaseDetailService.updPurchaseDetailNum(
                    any(), eq(1L), eq(3),
                    eq(StatusEnum.US_ENABLED.getCode()),
                    eq(StatusEnum.US_OCCUPY.getCode())))
                    .thenReturn(1);
            doNothing().when(prescriptionPhysicService).updConstAndIncomeById(any());
            // 第二个药品库存不足
            when(parchaseDetailMapper.selectCountForLock(2L, StatusEnum.US_ENABLED.getCode()))
                    .thenReturn(1);  // 需要 5，可用 1
            mockPhysicName(2L, "布洛芬");

            PrescriptionRequest req = new PrescriptionRequest();
            req.setPatient(1L);
            req.setUser(1L);
            req.setComments("多药品");
            PrescriptionRequest.Item item1 = new PrescriptionRequest.Item();
            item1.setPhysic(1L);
            item1.setNum(3);
            PrescriptionRequest.Item item2 = new PrescriptionRequest.Item();
            item2.setPhysic(2L);
            item2.setNum(5);
            req.setItems(Arrays.asList(item1, item2));

            assertThatThrownBy(() -> prescriptionService.createPrescriptionWithItems(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("库存不足")
                    .hasMessageContaining("布洛芬");

            // 验证第一个药品的处理已发生（但是事务在真实运行时回滚）
            verify(prescriptionPhysicService).addPrescriptionPhysic(any());
            verify(purchaseDetailService).updPurchaseDetailNum(
                    any(), eq(1L), eq(3), anyInt(), anyInt());
            verify(prescriptionPhysicService).updConstAndIncomeById(any());
        }
    }
}
