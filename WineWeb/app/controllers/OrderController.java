package controllers;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import LyLib.Utils.Msg;
import LyLib.Utils.PageInfo;
import LyLib.Utils.StrUtil;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import models.OrderModel;
import models.UserModel;
import models.common.CompanyModel;
import models.common.ResellerRecord;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.order_backend;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

public class OrderController extends Controller implements IConst {

    @Security.Authenticated(SecuredAdmin.class)
    public static Result backendPage() {
        return ok(order_backend.render());
    }

    public static char getRamdonLetter() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return chars.charAt((int) (Math.random() * 52));
    }

    @Security.Authenticated(Secured.class)
    public static Result addOrder() {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Msg<OrderModel> msg = new Msg<>();

        Form<OrderModel> httpForm = form(OrderModel.class).bindFromRequest();
        if (!httpForm.hasErrors()) {
            OrderModel formObj = httpForm.get();

            if (formObj.status != 0 || !StrUtil.isNull(formObj.payReturnCode) || !StrUtil.isNull(formObj.payReturnCode)
                    || !StrUtil.isNull(formObj.payReturnMsg) || !StrUtil.isNull(formObj.payResultCode)
                    || !StrUtil.isNull(formObj.payTransitionId) || !StrUtil.isNull(formObj.payAmount)
                    || !StrUtil.isNull(formObj.payBank) || !StrUtil.isNull(formObj.payRefOrderNo)
                    || !StrUtil.isNull(formObj.paySign) || !StrUtil.isNull(formObj.payTime)
                    || !StrUtil.isNull(formObj.payThirdPartyId) || !StrUtil.isNull(formObj.payThirdPartyUnionId)
                    || formObj.resellerProfit1 > 0 || formObj.resellerProfit2 > 0 || formObj.resellerProfit3 > 0
                    || formObj.productAmount > formObj.amount) {
                play.Logger.error("*******新增订单状态异常, 须检查. 发起IP: " + request().remoteAddress());
                return notFound("新增订单状态异常");
            }

            // 新订单状态必须为0新增
            formObj.status = 0;

            formObj.orderNo = DateUtil.Date2Str(new Date(), "yyyyMMddHHmmss") + getRamdonLetter();
            formObj.createClientIP = request().remoteAddress();
            formObj.productAmount = formObj.price * formObj.quantity;

            if (formObj.refResellerId != 0) {
                UserModel reseller = UserModel.find.byId(formObj.refResellerId);
                if (reseller != null) {
                    formObj.reseller = reseller;
                }
            }

            Ebean.save(formObj);

            msg.flag = true;
            msg.data = formObj;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + CREATE_SUCCESS);
        } else {
            msg.message = httpForm.errors().toString();
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
        }
        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(SecuredSuperAdmin.class)
    public static Result updateOrder(long id) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Msg<OrderModel> msg = new Msg<>();

        OrderModel found = OrderModel.find.byId(id);
        if (found == null) {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
            return ok(Json.toJson(msg));
        }

        Form<OrderModel> httpForm = form(OrderModel.class).bindFromRequest();

        if (!httpForm.hasErrors()) {
            OrderModel formObj = httpForm.get();

            // 逐个赋值
            found.logisticsCompany = formObj.logisticsCompany;
            found.numberOfLogistics = formObj.numberOfLogistics;
            // found.shipTimeStr = formObj.shipTimeStr;
            found.comment = formObj.comment;
            Ebean.update(found);

            // 用户密码不返回
            if (found.buyer != null)
                found.buyer.password = "";

            msg.flag = true;
            msg.data = found;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + UPDATE_SUCCESS);
        } else {
            msg.message = httpForm.errors().toString();
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
        }
        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(SecuredSuperAdmin.class)
    public static Result updateOrderStatus(long id, int status) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Msg<OrderModel> msg = new Msg<>();

        OrderModel found = OrderModel.find.byId(id);
        if (found == null) {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
            return ok(Json.toJson(msg));
        }

        // 更改订单状态

        if (status == 0 || status == 1 || status == 2 || status == 3 || status == 4 || status == 5 || status == 6
                || status == 7) {
            if (status == 4) {
                found.shipTimeStr = DateUtil.Date2Str(new Date());
            }
            found.status = status;
            Ebean.update(found);

            // 用户密码不返回
            if (found.buyer != null)
                found.buyer.password = "";

            msg.flag = true;
            msg.data = found;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + UPDATE_SUCCESS);
        } else {
            msg.message = "状态不对";
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
        }

        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(Secured.class)
    public static Result updateOrderStatusByUser(long id, int status) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Msg<OrderModel> msg = new Msg<>();

        OrderModel found = OrderModel.find.byId(id);
        if (found == null) {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
            return ok(Json.toJson(msg));
        }

        // 更改订单状态, 只能改取消和确认收货
        if (status == 2 || status == 5) {
            found.status = status;
            Ebean.update(found);

            // 用户密码不返回
            if (found.buyer != null)
                found.buyer.password = "";

            msg.flag = true;
            msg.data = found;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + UPDATE_SUCCESS);
        } else {
            msg.message = "状态不对";
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
        }

        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(SecuredAdmin.class)
    public static Result getAllOrders(Integer status, String keyword, Integer page, Integer size) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        if (size == 0)
            size = PAGE_SIZE;
        if (page <= 0)
            page = 1;

        Msg<List<OrderModel>> msg = new Msg<>();
        Page<OrderModel> records;

        if (status == -1) {
            if (StrUtil.isNull(keyword)) {
                // 默认不显示"已取消","已删除","支付失败","等待支付结果"
                records = OrderModel.find.where().ne("status", 2).ne("status", 3).ne("status", 8).ne("status", 9)
                        .orderBy("id desc").findPagingList(size).setFetchAhead(false).getPage(page - 1);
            } else {
                records = OrderModel.find.where()
                        .or(Expr.like("orderNo", "%" + keyword + "%"), Expr.like("buyer.nickname", "%" + keyword + "%"))
                        .orderBy("id desc").findPagingList(size).setFetchAhead(false).getPage(page - 1);
            }
        } else {
            records = OrderModel.find.where().eq("status", status).orderBy("id desc").findPagingList(size)
                    .setFetchAhead(false).getPage(page - 1);
        }

        if (records.getTotalRowCount() > 0) {
            msg.flag = true;

            PageInfo pageInfo = new PageInfo();
            pageInfo.current = page;
            pageInfo.total = records.getTotalPageCount();
            pageInfo.desc = records.getDisplayXtoYofZ("-", "/");
            pageInfo.size = size;
            if (records.hasPrev())
                pageInfo.hasPrev = true;
            if (records.hasNext())
                pageInfo.hasNext = true;

            // 用户密码不返回
            List<OrderModel> orders = records.getList();
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).buyer != null) {
                    orders.get(i).buyer.password = "";
                }
            }
            msg.data = orders;
            msg.page = pageInfo;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + records.getTotalRowCount());
        } else {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
        }
        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(Secured.class)
    public static Result getOrder(long id) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Msg<OrderModel> msg = new Msg<>();

        OrderModel found = OrderModel.find.byId(id);
        if (found != null) {

            // 用户密码不返回
            if (found.buyer != null)
                found.buyer.password = "";

            msg.flag = true;
            msg.data = found;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + found);
        } else {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + NO_FOUND);
        }
        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(Secured.class)
    public static Result getOrdersByUser(long refBuyerId) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Msg<List<OrderModel>> msg = new Msg<>();

        List<OrderModel> found = OrderModel.find.where().eq("refBuyerId", refBuyerId).orderBy("id desc").findList();
        if (found != null) {
            // 用户密码不返回
            for (int i = 0; i < found.size(); i++) {
                if (found.get(i).buyer != null) {
                    found.get(i).buyer.password = "";
                }
            }

            msg.flag = true;
            msg.data = found;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + found.size());
        } else {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + NO_FOUND);
        }
        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(Secured.class)
    public static Result getOrderByOrderNo(String orderNo) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Msg<OrderModel> msg = new Msg<>();

        OrderModel found = OrderModel.find.where().eq("orderNo", orderNo).findUnique();
        if (found != null) {

            // 用户密码不返回
            if (found.buyer != null)
                found.buyer.password = "";

            msg.flag = true;
            msg.data = found;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + found);
        } else {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + NO_FOUND);
        }
        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(Secured.class)
    public static Result getResellerOrders(Long id) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());

        Msg<List<OrderModel>> msg = new Msg<>();

        if (id == 0) {
            if (StrUtil.isNull(session(SESSION_USER_ID))) {
                msg.message = NO_FOUND;
                return ok(Json.toJson(msg));
            } else {
                id = Long.parseLong(session(SESSION_USER_ID));
            }
        }

        // 未支付的订单不显示
        List<OrderModel> records = OrderModel.find.where().and(Expr.eq("refResellerId", id), Expr.gt("status", 0))
                .orderBy("id desc").findList();
        // List<OrderModel> records =
        // OrderModel.find.where().and(Expr.eq("refResellerId",
        // id),Expr.gt("status", 0))
        // .orderBy("id desc").findList();

        if (records.size() > 0) {
            msg.flag = true;
            msg.data = records;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + records.size());
        } else {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
        }
        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(Secured.class)
    public static Result getResellerOrderAmount(Long id) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());

        Msg<Double> msg = new Msg<>();

        if (id == 0) {
            if (StrUtil.isNull(session(SESSION_USER_ID))) {
                msg.message = NO_FOUND;
                return ok(Json.toJson(msg));
            } else {
                id = Long.parseLong(session(SESSION_USER_ID));
            }
        }
        // 计算已确认/已计算佣金/已取消计算佣金的订单
        play.Logger.info("start do reseller order count: " + DateUtil.Date2Str(new Date()));
        List<Integer> statusList = new ArrayList<>();
        statusList.add(5);
        statusList.add(6);
        statusList.add(7);

        List<OrderModel> orders = OrderModel.find.where()
                .and(Expr.eq("refResellerId", id), Expr.in("status", statusList)).orderBy("id desc").findList();

        List<UserModel> allDownlineUsers = new ArrayList<>();

        // 找下线
        List<UserModel> downlineUsers1 = UserModel.find.where().eq("refUplineUserId", id).findList();

        play.Logger.info("1st level downline count: " + downlineUsers1.size());
        allDownlineUsers.addAll(downlineUsers1);

        // 找下线的下线
        for (UserModel downlineUser : downlineUsers1) {
            allDownlineUsers.addAll(UserModel.find.where().eq("refUplineUserId", downlineUser.id).findList());
        }
        play.Logger.info("total downline count: " + allDownlineUsers.size());

        for (UserModel downlines : allDownlineUsers) {
            orders.addAll(OrderModel.find.where().and(Expr.eq("refResellerId", downlines.id), Expr.eq("status", 5))
                    .orderBy("id desc").findList());
        }

        Double totalProductAmount = 0D;
        for (OrderModel record : orders) {
            totalProductAmount += record.productAmount;
        }

        if (orders.size() > 0) {
            msg.flag = true;
            msg.data = totalProductAmount;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + totalProductAmount);
        } else {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
        }
        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(SecuredSuperAdmin.class)
    public static Result doCalculate(Long id) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Msg<OrderModel> msg = new Msg<>();

        OrderModel order = OrderModel.find.byId(id);

        if (order != null) {
            // 已确认收货的订单才能分销, 订单有用户才能分销
            if (order.status == 5 && order.refBuyerId != 0 && order.refResellerId > 0) {
                // 1. 拿到分成比例
                // 2. 拿到订单的分销用户(即下单用户的上线)
                // 3. 拿到分销用户的上线(若有)及上上线(若有)
                // 4. 计算订单的商品的累计分销额
                // 5. 按分成比例设置订单的三层佣金
                // 6. 对分销用户(最多3个)进行佣金的加入

                CompanyModel companyInfo = CompanyModel.findAll().get(0);
                List<ResellerRecord> resultList = new ArrayList<>();
                resultList.add(new ResellerRecord(0));// A用户(订单买家的上线),分最多
                resultList.add(new ResellerRecord(0));// A的上线B用户
                resultList.add(new ResellerRecord(0));// B的上线C用户, 分最少

                play.Logger.info("执行分销计算， 于订单: " + order.orderNo);

                // 分销限制3层, 注意不一定每层都存在可拿佣金的用户(不存在上线或未开通分销都是有可能的)
                UserModel tempUser = UserModel.find.byId(order.buyer.refUplineUserId);
                for (int i = 0; i < resultList.size(); i++) {
                    if (tempUser != null && tempUser.isReseller && tempUser.userStatus < 1) {
                        // 存在该用户(并且不是上帝),
                        // 他是分销商,
                        // 且不被冻结删除,
                        // 才能参加分销
                        resultList.get(i).user = tempUser;

                        // 如果无上线, 则循环结束
                        if (resultList.get(i).user.refUplineUserId == -1)
                            break;

                        UserModel tempUser2 = UserModel.find.byId(resultList.get(i).user.refUplineUserId);
                        if (tempUser2.id != tempUser.id) {
                            tempUser = tempUser2;
                        }
                    }
                }

                // 将无下线的分销比例累加到上级
                if (resultList.get(2).user != null) {// 三级都有
                    resultList.get(2).rate = companyInfo.marketing1;
                    resultList.get(1).rate = companyInfo.marketing2;
                    resultList.get(0).rate = companyInfo.marketing3;
                } else {
                    if (resultList.get(1).user != null) {// 有一, 二级
                        resultList.get(1).rate = companyInfo.marketing1;
                        resultList.get(0).rate = companyInfo.marketing2 + companyInfo.marketing3;
                    } else {// 仅一级
                        resultList.get(0).rate = companyInfo.marketing1 + companyInfo.marketing2
                                + companyInfo.marketing3;
                    }
                }

                play.Logger.info("reseller list: " + resultList);

                // 取得订单下所有产品的累计分销额
                // Double totalAvailableResellerAmount = 0D;
                // for (ProductModel prod : order.orderProducts) {
                // totalAvailableResellerAmount += prod.resellerMark;
                // }

                // 取得订单额作为分销额(邮费等不包括)
                Double totalAvailableResellerAmount = order.productAmount;

                // 计算佣金
                DecimalFormat df = new DecimalFormat("#.##");
                for (int i = 0; i < resultList.size(); i++) {
                    ResellerRecord record = resultList.get(i);
                    if (record.user != null) {
                        record.profit = Double.parseDouble(df.format(record.rate * totalAvailableResellerAmount));
                        record.user.currentResellerProfit += record.profit;
                        Ebean.update(record.user);

                        if (i == 0) {
                            order.resellerProfit1 = record.profit;
                        }
                        if (i == 1) {
                            order.resellerProfit2 = record.profit;
                        }
                        if (i == 2) {
                            order.resellerProfit3 = record.profit;
                        }
                    }
                }

                // 改状态为"已执行分销"
                order.status = 6;
                Ebean.update(order);

                msg.flag = true;
                msg.data = order;
                play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + order.resellerProfit1 + ", "
                        + order.resellerProfit2 + ", " + order.resellerProfit3 + ". total: "
                        + totalAvailableResellerAmount.toString());
            } else {
                msg.message = "订单尚未确认收货, 或不存在上线分销商, 不能执行分销佣金计算";
                play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
            }
        } else {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + NO_FOUND);
        }
        return ok(Json.toJson(msg));
    }

    @Security.Authenticated(SecuredSuperAdmin.class)
    public static Result cancelCalculate(Long id) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Msg<OrderModel> msg = new Msg<>();

        OrderModel order = OrderModel.find.byId(id);
        if (order != null) {
            // 已分销的订单才能取消分销, 订单有用户才能分销
            if (order.status == 6 && order.refBuyerId != 0 && order.refResellerId > 0) {
                UserModel buyer = UserModel.find.byId(order.refBuyerId);

                if (buyer != null && buyer.refUplineUserId > 0) {
                    UserModel buyerUpline = UserModel.find.byId(buyer.refUplineUserId);

                    if (buyerUpline != null && buyerUpline.refUplineUserId > 0) {
                        buyerUpline.currentResellerAvailableAmount -= order.productAmount;
                        buyerUpline.currentResellerProfit -= order.resellerProfit1;
                        Ebean.update(buyerUpline);

                        UserModel buyerUplineUpline = UserModel.find.byId(buyerUpline.refUplineUserId);

                        if (buyerUplineUpline != null && buyerUplineUpline.refUplineUserId > 0) {
                            buyerUplineUpline.currentResellerAvailableAmount -= order.productAmount;
                            buyerUplineUpline.currentResellerProfit -= order.resellerProfit2;
                            Ebean.update(buyerUplineUpline);

                            UserModel buyerUplineUplineUpline = UserModel.find.byId(buyerUplineUpline.refUplineUserId);
                            if (buyerUplineUplineUpline != null) {
                                buyerUplineUplineUpline.currentResellerAvailableAmount -= order.productAmount;
                                buyerUplineUplineUpline.currentResellerProfit -= order.resellerProfit3;
                                Ebean.update(buyerUplineUplineUpline);
                            }
                        }
                    }
                }
                order.resellerProfit1 = 0d;
                order.resellerProfit2 = 0d;
                order.resellerProfit3 = 0d;

                // 改状态为"已取消分销"
                order.status = 7;
                Ebean.update(order);

                msg.flag = true;
                msg.data = order;
                play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + order.resellerProfit1 + ", "
                        + order.resellerProfit2 + ", " + order.resellerProfit3);
            } else {
                msg.message = "订单尚未分销, 不能执行取消分销";
                play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
            }
        } else {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + NO_FOUND);
        }
        return ok(Json.toJson(msg));
    }

    public static Result checkAndKillUselessOrders() {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());

        Msg<Integer> msg = new Msg<>();
        List<OrderModel> records = OrderModel.find.where().eq("status", 0).findList();
        Integer expireHours = 24;
        Long expireMillSecconds = expireHours * 60 * 60 * 1000L;

        Integer killOrderCount = 0;
        for (OrderModel order : records) {
            Date orderDate = DateUtil.Str2Date(order.createdAtStr);
            Date nowDate = new Date();
            Long MillSecconds = nowDate.getTime() - orderDate.getTime();

            if (MillSecconds >= expireMillSecconds) {
                order.status = 2;//自动设为已取消
                Ebean.update(order);
                killOrderCount++;
            }
        }

        // List<OrderModel> deliverRecords =
        // OrderModel.find.where().eq("status", 4).findList();
        // Integer deliverHours = 168;
        // long deliverMillSeconds = deliverHours * 60 * 60 * 1000L;
        // for (OrderModel order : deliverRecords) {
        // if (!StrUtil.isNull(order.shipTimeStr)) {
        // Date deliverDate = DateUtil.Str2Date(order.shipTimeStr);
        // if (deliverDate == null) {
        // play.Logger.error(DateUtil.Date2Str(new Date()) + "处理订单自动确认出错, 订单: "
        // + order.id + ", 订单发货时间: "
        // + order.shipTimeStr);
        // }
        // Date nowDate = new Date();
        // Long MillSeconds = nowDate.getTime() - deliverDate.getTime();
        // if (MillSeconds >= deliverMillSeconds) {
        // order.status = 5;
        // Ebean.update(order);
        // }
        // }
        // }

        if (records.size() > 0) {
            msg.flag = true;
            msg.data = killOrderCount;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + records.size());
        } else {
            msg.message = NO_FOUND;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
        }
        return ok(Json.toJson(msg));
    }

}
