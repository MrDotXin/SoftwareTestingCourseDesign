package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.bill.BillAddRequest;
import com.mrdotxin.propsmart.model.dto.bill.BillPayRequest;
import com.mrdotxin.propsmart.model.dto.bill.BillQueryRequest;
import com.mrdotxin.propsmart.model.dto.bill.BillUpdateRequest;
import com.mrdotxin.propsmart.model.entity.Bill;
import com.mrdotxin.propsmart.model.vo.BillVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 账单服务
 */
public interface BillService extends IService<Bill> {

    /**
     * 创建账单
     *
     * @param billAddRequest 账单创建请求
     * @param request HTTP请求
     * @return 账单ID
     */
    long addBill(BillAddRequest billAddRequest, HttpServletRequest request);

    /**
     * 删除账单
     *
     * @param id 账单ID
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean deleteBill(long id, HttpServletRequest request);

    /**
     * 更新账单
     *
     * @param billUpdateRequest 账单更新请求
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean updateBill(BillUpdateRequest billUpdateRequest, HttpServletRequest request);

    /**
     * 根据ID获取账单
     *
     * @param id 账单ID
     * @return 账单
     */
    BillVO getBillById(long id);

    /**
     * 获取账单分页
     *
     * @param billQueryRequest 查询条件
     * @return 账单分页
     */
    Page<BillVO> listBillByPage(BillQueryRequest billQueryRequest);

    /**
     * 支付账单
     *
     * @param billPayRequest 支付请求
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean payBill(BillPayRequest billPayRequest, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param billQueryRequest
     * @return
     */
    QueryWrapper<Bill> getQueryWrapper(BillQueryRequest billQueryRequest);

    /**
     * 账单对象转视图对象
     *
     * @param bill
     * @return
     */
    BillVO getBillVO(Bill bill);
}
