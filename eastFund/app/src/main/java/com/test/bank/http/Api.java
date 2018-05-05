package com.test.bank.http;

import com.test.bank.base.BaseBean;
import com.test.bank.bean.BankSmsCodeBean;
import com.test.bank.bean.BuyGoodFundBean;
import com.test.bank.bean.BuyGoodFundDetailBean;
import com.test.bank.bean.ChaseHotBean;
import com.test.bank.bean.FundHoldDetailBean;
import com.test.bank.bean.FundHomeBean;
import com.test.bank.bean.FundTradeRecordBean;
import com.test.bank.bean.FundTradeRecordDetailBean;
import com.test.bank.bean.FundTradeResultBean;
import com.test.bank.bean.FundTrendBean;
import com.test.bank.bean.GetOutBankCardInfoBean;
import com.test.bank.bean.GraspChanceBean;
import com.test.bank.bean.GraspChanceDetailBean;
import com.test.bank.bean.HotSearchBean;
import com.test.bank.bean.MakeMoneyBannerBean;
import com.test.bank.bean.MakeMoneyCurrentPlusBean;
import com.test.bank.bean.MakeMoneyListBean;
import com.test.bank.bean.MessageBean;
import com.test.bank.bean.OptionalFundBean;
import com.test.bank.bean.QuerySupportBankLimitDataBean;
import com.test.bank.bean.ResetLoginPwdBean;
import com.test.bank.bean.RiskEvaluationQuestionBean;
import com.test.bank.bean.RiskEvaluationResultBean;
import com.test.bank.bean.SearchResultBean;
import com.test.bank.bean.SmartQABean;
import com.test.bank.bean.SmsCheckCodeBean;
import com.test.bank.bean.SupportBankAndAmountBean;
import com.test.bank.bean.TestResultBean;
import com.test.bank.bean.UserInfo;
import com.test.bank.bean.UserInfoBean;
import com.test.bank.bean.VersionUpdateBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by 55 on 2017/11/6.
 */

public interface Api {
    /**
     * 抓机会列表
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/get_navcatch_list.do")
    Observable<BaseBean<List<GraspChanceBean>>> getGraspChanceList(@FieldMap Map<String, String> paramMap);

    /**
     * 买好基列表
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/get_nbfunds_list.do")
    Observable<BaseBean<List<BuyGoodFundBean>>> getBuyGoodFundList(@FieldMap Map<String, String> paramMap);

    /**
     * 赚钱首页-列表项
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/get_make_money_list.do")
    Observable<BaseBean<MakeMoneyListBean>> getMakeMoneyList(@FieldMap Map<String, String> paramMap);

    /**
     * 赚钱首页-banner
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/get_activity_list.do")
    Observable<BaseBean<MakeMoneyBannerBean>> getMakeMoneyBanner(@FieldMap Map<String, String> paramMap);

    /**
     * 赚钱首页-活期+
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/get_plan_baobao.do")
    Observable<BaseBean<List<MakeMoneyCurrentPlusBean>>> getMakeMoneyCurrentPlusList(@FieldMap Map<String, String> paramMap);

    /**
     * 赚钱/买好基 - 详情
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/get_plan_detail.do")
    Observable<BaseBean<BuyGoodFundDetailBean>> getBuyGoodFundDetail(@FieldMap Map<String, String> paramMap);


    /**
     * 追热点列表
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/get_hotspot_list.do")
    Observable<BaseBean<List<ChaseHotBean>>> getChaseHotList(@FieldMap Map<String, String> paramMap);


    /**
     * 抓机会详情-头部信息
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/get_navcatch_details.do")
    Observable<BaseBean<GraspChanceDetailBean>> getGraspChanceDetail(@FieldMap Map<String, String> paramMap);

    /**
     * 获取短信验证码
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/send_code.do")
    Observable<BaseBean<SmsCheckCodeBean>> getCheckCode(@FieldMap Map<String, String> paramMap);

    /**
     * 单只基金收益率走势
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/fund_trend.do")
    Observable<BaseBean<FundTrendBean>> getSingleFundTrend(@FieldMap Map<String, String> paramMap);


    /**
     * 搜索
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/search.do")
    Observable<BaseBean<SearchResultBean>> search(@FieldMap Map<String, String> paramMap);

    /**
     * 获取热搜榜数据
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/search_heat.do")
    Observable<BaseBean<HotSearchBean>> getHotSearchList(@FieldMap Map<String, String> paramMap);


    /**
     * 登录
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/login.do")
    Observable<BaseBean<UserInfo>> login(@FieldMap Map<String, String> paramMap);


    /**
     * 注册
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/register.do")
    Observable<BaseBean<UserInfo>> register(@FieldMap Map<String, String> paramMap);


    /**
     * 重置登录密码
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/rest_login_pwd.do")
    Observable<BaseBean<ResetLoginPwdBean>> resetLoginPwd(@FieldMap Map<String, String> paramMap);

    /**
     * 忘记登录密码
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/forget_login_pwd.do")
    Observable<BaseBean<ResetLoginPwdBean>> forgetLoginPwd(@FieldMap Map<String, String> paramMap);

    /**
     * 个人中心
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/user_info.do")
    Observable<BaseBean<UserInfoBean>> getUserInfo(@FieldMap Map<String, String> paramMap);


    /**
     * 重置交易密码
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/rest_tran_pwd.do")
    Observable<BaseBean<Boolean>> resetTradePwd(@FieldMap Map<String, String> paramMap);


    /**
     * 查询支持的银行卡限额数据
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/banks_limit.do")
    Observable<BaseBean<List<QuerySupportBankLimitDataBean>>> querySupportBankData(@FieldMap Map<String, String> paramMap);


    /**
     * 查看风险评测问题
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/queryrisk.do")
    Observable<BaseBean<RiskEvaluationQuestionBean>> queryRiskQuestion(@FieldMap Map<String, String> paramMap);


    /**
     * 查看风险评测问题
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/modifyrisk.do")
    Observable<BaseBean<RiskEvaluationResultBean>> submitRiskAnswers(@FieldMap Map<String, String> paramMap);

    /**
     * 消息列表
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/news_records.do")
    Observable<BaseBean<List<MessageBean>>> getMessageList(@FieldMap Map<String, String> paramMap);


    /**
     * 基金交易记录
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/fund_buy_records.do")
    Observable<BaseBean<List<FundTradeRecordBean>>> getFundTradeRecordList(@FieldMap Map<String, String> paramMap);

    /**
     * 交易记录详情
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/fund_records_detail.do")
    Observable<BaseBean<FundTradeRecordDetailBean>> getFundTradeRecordDetail(@FieldMap Map<String, String> paramMap);


    /**
     * 买入或卖出页面数据
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/get_buy_or_redemption_info.do")
    Observable<BaseBean<GetOutBankCardInfoBean>> getBuyInData(@FieldMap Map<String, String> paramMap);


    /**
     * 基金购买
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/fund_buy.do")
    Observable<BaseBean<FundTradeResultBean>> buyFund(@FieldMap Map<String, String> paramMap);

    /**
     * 基金赎回
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/fund_redemption.do")
    Observable<BaseBean<FundTradeResultBean>> saleOutFund(@FieldMap Map<String, String> paramMap);

    /**
     * 基金首页
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/acct_funds.do")
    Observable<BaseBean<FundHomeBean>> getFundHomeData(@FieldMap Map<String, String> paramMap);


    /**
     * 撤单
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/fund_cancel_trade.do")
    Observable<BaseBean<FundTradeResultBean>> cancelFundTrade(@FieldMap Map<String, String> paramMap);

    /**
     * 基金持仓详情
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/acct_funddetail.do")
    Observable<BaseBean<FundHoldDetailBean>> getHoldFundDetail(@FieldMap Map<String, String> paramMap);


    /**
     * 智能客服：获取答案
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/customerService.do")
    Observable<BaseBean<SmartQABean>> getSmartAnswer(@FieldMap Map<String, String> paramMap);


    /**
     * 开户--获取银行发送的验证码
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/check_bank_card.do")
    Observable<BaseBean<BankSmsCodeBean>> getBankCheckCode(@FieldMap Map<String, String> paramMap);

    /**
     * 开户
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/create_acct.do")
    Observable<BaseBean<String>> openAccount(@FieldMap Map<String, String> paramMap);


    /**
     * 开户-查看支持银行及额度
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/banks_limit.do")
    Observable<BaseBean<List<SupportBankAndAmountBean>>> checkSupportBankAndAmount(@FieldMap Map<String, String> paramMap);


    /**
     * 搜索-添加自选
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/personCollection/addCollection.do")
    Observable<BaseBean<OptionalFundBean>> collectFund(@FieldMap Map<String, String> paramMap);

    /**
     * 搜索-删除自选
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/personCollection/deleteCollection.do")
    Observable<BaseBean<OptionalFundBean>> deCollectFund(@FieldMap Map<String, String> paramMap);

    /**
     * 测一测提交答案
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/check/answer.do")
    Observable<BaseBean<List<TestResultBean>>> submitTestAnswer(@FieldMap Map<String, String> paramMap);


    /**
     * 测一测提交答案
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/security/check/result.do")
    Observable<BaseBean<List<TestResultBean>>> getTestResult(@FieldMap Map<String, String> paramMap);

    /**
     * 检查版本更新
     *
     * @param paramMap
     * @return
     */
    @FormUrlEncoded
    @POST("v1/version.do")
    Observable<BaseBean<VersionUpdateBean>> checkoutVersionUpdate(@FieldMap Map<String, String> paramMap);

}
