package controllers;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import LyLib.Utils.Msg;
import LyLib.Utils.StrUtil;
import com.avaje.ebean.Ebean;

import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import models.*;
import models.common.CompanyModel;
import net.sf.uadetector.ReadableDeviceCategory;
import net.sf.uadetector.ReadableOperatingSystem;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import play.Play;
import play.cache.Cached;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static play.data.Form.form;

public class Application extends Controller implements IConst {

    public static class LoginParser {

        public String username;
        public String password;

        public String validate() {
            if (password != null && password.length() < 32) {
                password = LyLib.Utils.MD5.getMD5(password);
            }
            if (UserModel.authenticate(username, password) == null) {
                return "用户名或密码不正确";
            }
            return null;
        }
    }

    public static Result login() {
        UserModel userModel = UserModel.findByloginName(session(SESSION_USER_NAME));
        if (userModel != null && userModel.userRole == 2) {
            // return redirect("assets/backend/index.html#/");
            return ok(cms.render());
        } else
            return ok(login.render(form(LoginParser.class)));
    }

    public static Result backendLogin() {
        UserModel userModel = UserModel.findByloginName(session(SESSION_USER_NAME));
        if (userModel != null && userModel.userRole == 2) {
            return redirect(routes.OrderController.backendPage());
        } else
            return ok(backend_login.render(form(LoginParser.class)));
    }

    public static Result logout() {
        session().clear();
        flash("logininfo", "您已登出,请重新登录");
        return redirect(routes.Application.login());
    }

    public static Result backendLogout() {
        session().clear();
        flash("logininfo", "您已登出,请重新登录");
        return redirect(routes.Application.backendLogin());
    }

    public static Result backendPage() {
        return redirect(routes.OrderController.backendPage());
    }

    public static Result indexPage() {
        return ok(index.render());
    }

    public static Result errorPage() {
        return ok(errpage.render());
    }

    public static Result blank() {
        return ok(blank.render());
    }

    public static Result newsPage() {
        return ok(news.render());
    }

    public static Result news2Page() {
        return ok(news2.render());
    }

    public static Result fondOutRequestPage() {
        return ok(fondOutRequest.render());
    }

    public static Result checkAlive() {
        return ok("alive");
    }

    public static Result blankPage4Platform() {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri());
        String userAgentStr = request().getHeader("User-Agent");

        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        ReadableUserAgent agent = parser.parse(userAgentStr);
        ReadableDeviceCategory device = agent.getDeviceCategory();
//        ReadableOperatingSystem os = agent.getOperatingSystem();

        // if pc
        if ("personal computer".equals(device.getCategory().getName().toLowerCase())) {
            play.Logger.info("FROM PC");
            return ok(pc_index.render());
        } else {// if 手机
            //if 微信浏览器
            if (request().getHeader("User-Agent").indexOf("MicroMessenger") > -1) {
                play.Logger.info("FROM WEIXIN");
                return ok(blank4Weixin.render());
            } else {
                // if 非微信手机浏览器
                play.Logger.info("FROM PHONE BROWSER");
                return ok(pc_index.render());
            }
        }
    }

    public static Result blankPage4WeixinOpenId() {
        return ok(blank4Weixin.render());
    }

    public static Result recruitmentPage() {
        return ok(recruitment.render());
    }

    public static Result winePcPage() {
        // play.Logger.info(DateUtil.Date2Str(new Date()) + " - " +
        // request().method() + ": " + request().uri());
        return ok(pc_index.render());
    }

    public static Result themeOrderPage() {
        return ok(themes.render());
    }

    public static Result charitablePage() {
        return ok(charitable.render());
    }

    public static Result themeProducPage() {
        return ok(themeProduct.render());
    }

    public static Result detailsPage() {
        return ok(details.render());
    }

    public static Result makePage() {
        return ok(make.render());
    }

    public static Result numCenterPage() {
        return ok(num_center.render());
    }

    public static Result pcMyorderPage() {
        return ok(pc_myorder.render());
    }

    public static Result pcOrderPage() {
        return ok(pc_order.render());
    }

    public static Result pcPayPage() {
        return ok(pc_pay.render());
    }

    public static Result productAddPage() {
        return ok(product_add.render());
    }

    public static Result themePage(long id) {
        return ok(themePct.render());
    }

    public static Result aboutUsPage() {
        return ok(aboutUs.render());
    }

    public static Result helpPage() {
        return ok(pc_help.render());
    }

    public static Result shoppingPage() {
        return ok(pc_shopping.render());
    }

    public static Result winePage(String resellerCode) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri());

        play.Logger.info("loading with session: " + session("WX_OPEN_ID"));
        play.Logger.info("resellerCode: " + resellerCode);

        if (session("WX_OPEN_ID") == null || !StrUtil.isNull(resellerCode)) {
            String oauthUrl =
                    "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxc2d78bf38b0f2897&redirect_uri=http%3A%2F%2Fwww.longxin9.com%2Fdowxuser%3FresellerCode="
                            + resellerCode + "%26path=home" +
                            "&response_type=code&scope=snsapi_base#wechat_redirect";
            // play.Logger.info("wx oauth url: " + oauthUrl); return
            return redirect(oauthUrl);
        } else {
            play.Logger.info("wx open id: " + session("WX_OPEN_ID"));
            return ok(wine.render());
        }

//        session("userid", "4");
//        return ok(wine.render());

        // get oauth url from wx tool
        // WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
        // play.Logger.info("create WxMpInMemoryConfigStorage class");
        // config.setAppId("wxc2d78bf38b0f2897"); // 设置微信公众号的appid
        // config.setSecret("de531f5fd994fed8c9bf0a46d268eec1"); //
        // config.setToken("LongXinWeb"); // 设置微信公众号的token
        // config.setAesKey("txbQCeH8QKI3HHicZEM1OPX4D8ojvTsAvblEkNnenOB"); //
        //
        // WxMpService wxService = new WxMpServiceImpl();
        // play.Logger.info("create WxMpService class");
        // wxService.setWxMpConfigStorage(config);
        // play.Logger.info("setWxMpConfigStorage");
        //
        // String redirectUrlAfterOAuth = "http://www.longxin9.com";
        // String oauthUrl =
        // wxService.oauth2buildAuthorizationUrl(redirectUrlAfterOAuth,
        // WxConsts.OAUTH2_SCOPE_BASE,
        // null);
        //
        // play.Logger.info("oauth url: " + oauthUrl);

        // try {
        // WxMpUserList wxUserList = wxService.userList("");
        // play.Logger.info("wxUserList: " + wxUserList.getCount());
        // String lang = "zh_CN"; // 语言
        // for (String userOpenIds : wxUserList.getOpenIds()) {
        // WxMpUser user = wxService.userInfo(userOpenIds, lang);
        // play.Logger.info(String.format("userId: %s, userName: %s, unionId:
        // %s", user.getOpenId(),
        // user.getNickname(), user.getUnionId()));
        // }
        // } catch (WxErrorException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // String accessToken = config.getAccessToken();
        // play.Logger.info("accessToken: " + accessToken);

        // wxService.oauth2buildAuthorizationUrl(WxConsts.OAUTH2_SCOPE_USER_INFO,
        // null);

        // 用户的openid在下面地址获得
        // https://mp.weixin.qq.com/debug/cgi-bin/apiinfo?t=index&type=用户管理&form=获取关注者列表接口%20/user/get
        // String openid = "...";
        // WxMpCustomMessage message =
        // WxMpCustomMessage.TEXT().toUser(openid).content("Hello
        // World").build();
        // try {
        // wxService.customMessageSend(message);
        // } catch (WxErrorException e) {
        // // TODO Auto-generated catch block
        // // e.printStackTrace();
        // }
    }

    public static Result marryPage(long id) {
        return ok(marry.render());
    }

    public static Result WproductPage(long id) {
        return ok(Wproduct.render());
    }

    public static Result orderPage() {
        return ok(order.render());
    }

    public static Result payPage() {
        return ok(pay.render());
    }

    public static Result locationPage() {
        return ok(location.render());
    }

    public static Result userCenterPage() {
        play.Logger.info("loading /w/userCenter with session: " + session("WX_OPEN_ID"));

        if (session("WX_OPEN_ID") == null) {
            String oauthUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxc2d78bf38b0f2897&redirect_uri=http%3A%2F%2Fwww.longxin9.com%2Fdowxuser%3FresellerCode=%26path=userCenter&response_type=code&scope=snsapi_base#wechat_redirect";
            return redirect(oauthUrl);
        } else {
            play.Logger.info("wx open id: " + session("WX_OPEN_ID"));
            return ok(userCenter.render());
        }
    }

    public static Result myLocationPage() {
        return ok(myLocation.render());
    }

    public static Result myOrderPage() {
        return ok(myOrder.render());
    }

    public static Result distributorPage() {
        return ok(distributor.render());
    }

    public static Result DistributionOrderPage() {
        return ok(DistributionOrder.render());
    }

    public static Result QRcodePage() {
        return ok(QRcode.render());
    }

    public static Result teamPage() {
        return ok(team.render());
    }

    public static Result processPage() {
        return ok(process.render());
    }

    public static Result OrderMessagePage() {
        return ok(OrderMessage.render());
    }

    public static Result allProductPage() {
        return ok(allProduct.render());
    }

    public static Result invoiceTitlePage() {
        return ok(invoiceTitle.render());
    }

    public static Result collectPage() {
        return ok(collect.render());
    }

    public static Result aboutPage() {
        return ok(about.render());
    }

    public static Result weixinPayPage() {
        return ok(weixinPay.render());
    }

    /**
     * Handle login form submission.
     */
    // 登录验证
    public static Result authenticate() {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());
        Form<LoginParser> loginForm = form(LoginParser.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + loginForm.errors().toString());
            return badRequest(login.render(loginForm));// @form.globalError.message
        } else {
            UserModel userModel = UserModel.findByloginName(loginForm.get().username);
            session().clear();
            session(SESSION_USER_NAME, userModel.loginName);
            session(SESSION_USER_ID, userModel.id.toString());
            if (userModel != null) {
                Integer role = userModel.userRole;
                session(SESSION_USER_ROLE, role.toString());
                if (role > 0) {
                    return ok(wine.render());
                } else {
                    // return redirect("assets/backend/index.html#/");
                    return forbidden("登录失败");
                }
            }
            return redirect(routes.Application.login());
        }
    }

    // 登录验证
    public static Result backendAuthenticate() {
        Form<LoginParser> loginForm = form(LoginParser.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            play.Logger.info(DateUtil.Date2Str(new Date()) + " form error: " + loginForm.errors().toString());
            // return badRequest(login.render(loginForm));//
            flash("logininfo", "登录失败,请重试");
            return redirect(routes.Application.backendLogin());
        } else {
            UserModel userModel = UserModel.findByloginName(loginForm.get().username);
            session().clear();
            session(SESSION_USER_NAME, userModel.loginName);
            session(SESSION_USER_ID, userModel.id.toString());
            if (userModel != null) {
                Integer role = userModel.userRole;
                session(SESSION_USER_ROLE, role.toString());
                if (role > 0) {// 1管理员, 2超级管理员
                    // TODO: 检查当前登录和最后一次登录IP, 如果不同, 要报警通知
                    // 更新最后一次登录的IP
                    userModel.lastLoginIP = request().remoteAddress();
                    Ebean.update(userModel);
                    return redirect(routes.OrderController.backendPage());
                } else {
                    // return redirect("assets/backend/index.html#/");
                    return forbidden("您没有权限登录后台");
                }
            }
            return redirect(routes.Application.backendLogin());
        }
    }

    // @Security.Authenticated(Secured.class)
    // @Cached(key = "showImage")
    public static Result showImage(String filename) {
        String path = Play.application().path().getPath() + "/public/upload/" + filename;

        try {
            response().setContentType("image");
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    IOUtils.toByteArray(new FileInputStream(new File(path))));
            return ok(bais);
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return notFound(filename + " is Not Found!");
    }

    // @Security.Authenticated(Secured.class)
    public static Result showBarcode(String filename) {
        String path = Play.application().path().getPath() + "/public/barcode/" + filename;

        try {
            response().setContentType("image");
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    IOUtils.toByteArray(new FileInputStream(new File(path))));
            return ok(bais);
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return notFound(filename + " barcode is Not Found!");
    }

    // @Security.Authenticated(Secured.class)
    // @Cached(key = "showImg")
    public static Result showImg(String folder, String filename) {
        String path = Play.application().path().getPath() + "/public/" + folder + "/" + filename;

        try {
            response().setContentType("image");
            return ok(getImageByte(path));
        } catch (IOException ex) {
            play.Logger.error(DateUtil.Date2Str(new Date()) + " - 找不到图片: " + folder + filename);
        }
        return notFound(folder + filename + " is Not Found!");
    }

    public static ByteArrayInputStream getImageByte(String path) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(new File(path))));
        return bais;
    }

    // @Security.Authenticated(Secured.class)
    public static Result uploadImage() {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());

        Msg<String> msg = new Msg<>();

        Http.MultipartFormData body = request().body().asMultipartFormData();

        Map map = body.asFormUrlEncoded();
        if (!map.containsKey("className") || !map.containsKey("property")) {
            msg.message = PARAM_ISSUE;
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
            return ok(Json.toJson(msg));
        }
        Long cid = 0l;
        if (map.containsKey("cid")) {
            cid = Long.parseLong(form().bindFromRequest().data().get("cid"));
        }
        String className = form().bindFromRequest().data().get("className");
        String property = form().bindFromRequest().data().get("property");

        Http.MultipartFormData.FilePart imgFile = body.getFile("file");
        if (imgFile != null) {
            // 图片地址及文件名, 以毫秒命名的文件名如"1449837445671"
            String path = Play.application().path().getPath() + "/public/upload/";
            String destFileName = String.valueOf(System.currentTimeMillis());

            String contentType = imgFile.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                msg.message = "error:not image file";
                play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                return ok(Json.toJson(msg));
            }

            File file = imgFile.getFile();
            try {
                // 生成原始图片
                FileUtils.copyFile(file, new File(path + destFileName));
                play.Logger.info(DateUtil.Date2Str(new Date()) + " - upload img success");
                // 生成缩略图
                String thumbNailPath = Play.application().path().getPath() + "/public/thumb/";
                try {
                    if (!GenerateThumbNailImg(path + destFileName, thumbNailPath + destFileName, 300F))
                        play.Logger.info(DateUtil.Date2Str(new Date()) + " - generate thumbnail img issue: unknown");
                    else
                        play.Logger.info(DateUtil.Date2Str(new Date()) + " - generate thumbnail img success");

                } catch (Exception ex) {
                    play.Logger.error(
                            DateUtil.Date2Str(new Date()) + " - generate thumbnail img issue: " + ex.getMessage());

                    //若生成缩略图出错则直接把原图拷贝到thumb目录
                    FileUtils.copyFile(file, new File(thumbNailPath + destFileName));
                }

                // 不指定ID也可以上传图片, 直接返回文件名
                if (cid == 0l) {
                    msg.flag = true;
                    msg.data = destFileName;
                    play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + destFileName);
                    return ok(Json.toJson(msg));
                }

                // 更新模型的字段为文件名
                if ("CompanyModel".equals(className)) {
                    CompanyModel found = CompanyModel.find.byId(cid);
                    if (found != null) {
                        if ("logo1".equals(property)) {
                            found.logo1 = destFileName;
                            Ebean.update(found);
                        } else if ("barcodeImg1".equals(property)) {
                            found.barcodeImg1 = destFileName;
                            Ebean.update(found);
                        } else if ("barcodeImg2".equals(property)) {
                            found.barcodeImg2 = destFileName;
                            Ebean.update(found);
                        } else if ("barcodeImg3".equals(property)) {
                            found.barcodeImg3 = destFileName;
                            Ebean.update(found);
                        } else {
                            msg.message = PARAM_ISSUE;
                            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                            return ok(Json.toJson(msg));
                        }
                    } else {
                        msg.message = NO_FOUND;
                        play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                        return ok(Json.toJson(msg));
                    }
                } else if ("CatalogModel".equals(className)) {
                    CatalogModel found = CatalogModel.find.byId(cid);
                    if (found != null) {
                        if ("images".equals(property)) {
                            if (LyLib.Utils.StrUtil.isNull(found.images)) {
                                found.images = destFileName;
                            } else {
                                found.images += "," + destFileName;
                            }
                            Ebean.update(found);
                        } else if ("smallImages".equals(property)) {
                            found.smallImages = destFileName;
                            Ebean.update(found);
                        } else {
                            msg.message = PARAM_ISSUE;
                            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                            return ok(Json.toJson(msg));
                        }
                    } else {
                        msg.message = NO_FOUND;
                        play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                        return ok(Json.toJson(msg));
                    }
                } else if ("InfoModel".equals(className)) {
                    InfoModel found = InfoModel.find.byId(cid);
                    if (found != null) {
                        if ("images".equals(property)) {
                            if (LyLib.Utils.StrUtil.isNull(found.images)) {
                                found.images = destFileName;
                            } else {
                                found.images += "," + destFileName;
                            }
                            Ebean.update(found);
                        } else if ("smallImages".equals(property)) {
                            found.smallImages = destFileName;
                            Ebean.update(found);
                        } else {
                            msg.message = PARAM_ISSUE;
                            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                            return ok(Json.toJson(msg));
                        }
                    } else {
                        msg.message = NO_FOUND;
                        play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                        return ok(Json.toJson(msg));
                    }
                } else if ("ProductModel".equals(className)) {
                    ProductModel found = ProductModel.find.byId(cid);
                    if (found != null) {
                        if ("images".equals(property)) {
                            if (LyLib.Utils.StrUtil.isNull(found.images)) {
                                found.images = destFileName;
                            } else {
                                found.images += "," + destFileName;
                            }
                            Ebean.update(found);
                        } else if ("description".equals(property)) {
                            if (LyLib.Utils.StrUtil.isNull(found.description)) {
                                found.description = destFileName;
                            } else {
                                found.description += "," + destFileName;
                            }
                            Ebean.update(found);
                        } else if ("productImage".equals(property)) {
                            found.productImage = destFileName;
                            Ebean.update(found);
                        } else {
                            msg.message = PARAM_ISSUE;
                            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                            return ok(Json.toJson(msg));
                        }
                    } else {
                        msg.message = NO_FOUND;
                        play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                        return ok(Json.toJson(msg));
                    }
                } else if ("ThemeModel".equals(className)) {
                    ThemeModel found = ThemeModel.find.byId(cid);
                    if (found != null) {
                        if ("images".equals(property)) {
                            if (LyLib.Utils.StrUtil.isNull(found.images)) {
                                found.images = destFileName;
                            } else {
                                found.images += "," + destFileName;
                            }
                            Ebean.update(found);
                        } else {
                            msg.message = PARAM_ISSUE;
                            play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                            return ok(Json.toJson(msg));
                        }
                    } else {
                        msg.message = NO_FOUND;
                        play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                        return ok(Json.toJson(msg));
                    }
                }
                msg.flag = true;
                msg.data = destFileName;
                play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + destFileName);
                return ok(Json.toJson(msg));
            } catch (IOException e) {
                msg.message = e.getMessage();
                play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
                return ok(Json.toJson(msg));
            }
        }
        msg.message = "error:Missing file";
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
        return ok(Json.toJson(msg));
    }

    public static boolean GenerateThumbNailImg(String baseFilePath, String thumbNailPath, float tagsize)
            throws Exception {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());

        if (tagsize == 0)
            tagsize = 100;

        String newUrl = thumbNailPath;
        java.awt.Image bigJpg = javax.imageio.ImageIO.read(new java.io.File(baseFilePath));

        if (bigJpg == null) {
            return false;
        }

        int old_w = bigJpg.getWidth(null);
        int old_h = bigJpg.getHeight(null);
        int new_w = 0;
        int new_h = 0;

        float tempdouble;
        tempdouble = old_w > old_h ? old_w / tagsize : old_h / tagsize;
        new_w = Math.round(old_w / tempdouble);
        new_h = Math.round(old_h / tempdouble);

        java.awt.image.BufferedImage tag = new java.awt.image.BufferedImage(new_w, new_h,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        tag.getGraphics().drawImage(bigJpg, 0, 0, new_w, new_h, null);

        try {
            File outputfile = new File(newUrl);
            ImageIO.write(tag, "png", outputfile);
        } catch (IOException e) {

        }
        return true;
    }

    @Security.Authenticated(SecuredSuperAdmin.class)
    public static Result generateAllThumbNailImg(float tagsize) {
        play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
                + " | DATA: " + request().body().asJson());

        if (tagsize == 0)
            tagsize = 300;

        String path = Play.application().path().getPath() + "/public/upload/";
        File file = new File(path);
        String[] fileNameList = file.list();

        String thumbNailPath = Play.application().path().getPath() + "/public/thumb/";
        for (String fileName : fileNameList) {
            play.Logger.info(DateUtil.Date2Str(new Date()) + " - generate thumb nail img: " + fileName);
            // 生成缩略图
            try {
                if (!GenerateThumbNailImg(path + fileName, thumbNailPath + fileName, tagsize))
                    play.Logger.info(DateUtil.Date2Str(new Date()) + " - generate thumbnail img issue: unknown");
                else
                    play.Logger.info(DateUtil.Date2Str(new Date()) + " - generate thumbnail img success");
            } catch (Exception ex) {
                play.Logger
                        .error(DateUtil.Date2Str(new Date()) + " - generate thumbnail img issue: " + ex.getMessage());
                return notFound("生成图片的缩略图出错");
            }
        }
        return ok("已生成所有图片的缩略图");
    }
}
