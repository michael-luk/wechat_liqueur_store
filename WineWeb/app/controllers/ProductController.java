package controllers;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import LyLib.Utils.Msg;
import LyLib.Utils.PageInfo;
import LyLib.Utils.StrUtil;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import models.CatalogModel;
import models.OrderModel;
import models.ProductModel;
import models.UserModel;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.product_backend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

public class ProductController extends Controller implements IConst {

	@Security.Authenticated(SecuredAdmin.class)
	public static Result backendPage() {
		return ok(product_backend.render());
	}

	@Security.Authenticated(SecuredSuperAdmin.class)
	public static Result addProduct() {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<ProductModel> msg = new Msg<>();

		Form<ProductModel> httpForm = form(ProductModel.class).bindFromRequest();
		if (!httpForm.hasErrors()) {
			ProductModel formObj = httpForm.get();
			// if (StrUtil.isNull(formObj.images)) {
			// formObj.images = "default_product_image.png";
			// }

			Ebean.save(formObj);
			if (formObj.catalogs.size() > 0) {
				for (CatalogModel cata : formObj.catalogs) {
					CatalogModel dbCata = CatalogModel.find.byId(cata.id);
					if (!dbCata.products.contains(formObj)) {
						dbCata.products.add(formObj);
						Ebean.update(dbCata);
					}
				}
			}
			msg.flag = true;
			msg.data = formObj;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + CREATE_SUCCESS);
		} else {
			msg.message = httpForm.errors().toString();
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
		}
		return ok(Json.toJson(msg));
	}

	// @Security.Authenticated(Secured.class)
	public static Result getAllProducts(Long catalogId, String keyword, Integer page, Integer size, String orderBy,
			String sort) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		if (size == 0)
			size = PAGE_SIZE;
		if (page <= 0)
			page = 1;
		if (StrUtil.isNull(orderBy))
			orderBy = "isHotSale desc, soldNumber desc";
		else {
			if (!StrUtil.isNull(sort))
				orderBy = orderBy + " " + sort;
		}

		Msg<List<ProductModel>> msg = new Msg<>();
		Page<ProductModel> records;

		if (catalogId > 0) {
			// 此处"catalogs.id = catalogId", 即可取出catalogs数组内的catalog中id相等的对象
			records = ProductModel.find.where().eq("catalogs.id", catalogId).orderBy(orderBy).findPagingList(size)
					.setFetchAhead(false).getPage(page - 1);
		} else {
			if (StrUtil.isNull(keyword)) {
				records = ProductModel.find.orderBy(orderBy).findPagingList(size).setFetchAhead(false)
						.getPage(page - 1);
			} else {
				records = ProductModel.find.where().or(Expr.like("id", "%" + keyword + "%"),Expr.like("name", "%" + keyword + "%")).orderBy(orderBy)
						.findPagingList(size).setFetchAhead(false).getPage(page - 1);
			}
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

			msg.data = records.getList();
			msg.page = pageInfo;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + records.getTotalRowCount());
		} else {
			msg.message = NO_FOUND;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
		}
		return ok(Json.toJson(msg));
	}

	// @Security.Authenticated(Secured.class)
	public static Result getProduct(long id) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<ProductModel> msg = new Msg<>();

		ProductModel found = ProductModel.find.byId(id);
		if (found != null) {
			msg.flag = true;
			msg.data = found;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + found);
		} else {
			msg.message = NO_FOUND;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + NO_FOUND);
		}
		return ok(Json.toJson(msg));
	}

	@Security.Authenticated(SecuredSuperAdmin.class)
	public static Result deleteProduct(long id) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<ProductModel> msg = new Msg<>();

		ProductModel found = ProductModel.find.byId(id);
		if (found != null) {
			// 解除多对多的关联
			for (CatalogModel catalog : found.catalogs) {
				catalog.products.remove(found);
				Ebean.update(catalog);
			}
			for (UserModel user : found.favoriteUsers) {
				user.favoriteProducts.remove(found);
				Ebean.update(user);
			}
			List<OrderModel> relatedOrders = OrderModel.find.where().eq("orderProducts.id", found.id).findList();
			for (OrderModel order : relatedOrders) {
				order.orderProducts.remove(found);
				Ebean.update(order);
				Ebean.delete(order);
			}
			found.favoriteUsers = new ArrayList<>();
			found.catalogs = new ArrayList<>();
			found.orders = new ArrayList<>();
			Ebean.update(found);
			Ebean.delete(found);
			msg.flag = true;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + DELETE_SUCCESS);
		} else {
			msg.message = NO_FOUND;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + NO_FOUND);
		}
		return ok(Json.toJson(msg));
	}

	@Security.Authenticated(SecuredSuperAdmin.class)
	public static Result updateProduct(long id) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<ProductModel> msg = new Msg<>();

		ProductModel found = ProductModel.find.byId(id);
		if (found == null) {
			msg.message = NO_FOUND;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
			return ok(Json.toJson(msg));
		}

		Form<ProductModel> httpForm = form(ProductModel.class).bindFromRequest();

		if (!httpForm.hasErrors()) {
			ProductModel formObj = httpForm.get();

			// 逐个赋值
			found.name = formObj.name;
			found.images = StrUtil.isNull(formObj.images) || ",".equals(formObj.images) ? "default_product_image.png"
					: formObj.images;
			found.shortDesc = formObj.shortDesc;
			found.tags = formObj.tags;
			found.alcoholDegree = formObj.alcoholDegree;
			found.ml = formObj.ml;
			found.bottleSpec = formObj.bottleSpec;
			found.decoration = formObj.decoration;
			found.canPaint = formObj.canPaint;
			found.canCarve = formObj.canCarve;
			found.resellerMark = formObj.resellerMark;
			found.flavor = formObj.flavor;
			found.productionCity = formObj.productionCity;
			found.brand = formObj.brand;
			found.source = formObj.source;
			found.totalWeight = formObj.totalWeight;
			found.productionLicense = formObj.productionLicense;
			found.factoryLocation = formObj.factoryLocation;
			found.price = formObj.price;
			found.refPrice = formObj.refPrice;
			found.originalPrice = formObj.originalPrice;
			found.isHotSale = formObj.isHotSale;
			found.soldNumber = formObj.soldNumber;
			found.thumbUp = formObj.thumbUp;
			found.previewImage = formObj.previewImage;
			found.description = formObj.description;
			found.spec = formObj.spec;
			found.notice = formObj.notice;
			found.comment = formObj.comment;

			// play.Logger.info("form product catalogs: " +
			// formObj.catalogs.size());
			// play.Logger.info("db product catalogs: " +
			// found.catalogs.size());

			// 先清掉要修改的产品下的主题下的产品
			for (CatalogModel dbCatalog : found.catalogs) {
				if (dbCatalog.products.contains(found)) {
					dbCatalog.products.remove(found);
					Ebean.update(dbCatalog);
				}
			}

			// 清掉产品一方
			found.catalogs = new ArrayList<>();
			Ebean.update(found);

			// 两边加回
			List<CatalogModel> dbCataList = CatalogModel.findAll();
			for (CatalogModel jsonCatalog : formObj.catalogs) {
				for (CatalogModel dbCata : dbCataList) {
					if (dbCata.id == jsonCatalog.id) {
						if (!found.catalogs.contains(dbCata)) {
							found.catalogs.add(dbCata);
						}
						if (!dbCata.products.contains(found)) {
							dbCata.products.add(found);
							Ebean.update(dbCata);
						}
					}

				}
			}
			Ebean.update(found);

			msg.flag = true;
			msg.data = found;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + UPDATE_SUCCESS);

		} else {
			msg.message = httpForm.errors().toString();
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
		}
		return ok(Json.toJson(msg));
	}

	@Security.Authenticated(Secured.class)
	public static Result searchProduct(String filter, String flavor, boolean like, String orderBy, String order,
			int page, int size) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<List<ProductModel>> msg = new Msg<>();

		if (StrUtil.isNull(filter) || StrUtil.isNull(flavor)) {
			msg.message = PARAM_ISSUE;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
			return ok(Json.toJson(msg));
		}

		if (size == 0)
			size = PAGE_SIZE;
		if (page <= 0)
			page = 1;

		Page<ProductModel> records;

		if (like) {
			records = ProductModel.find.where().like(filter, "%" + flavor + "%").orderBy(orderBy + " " + order)
					.findPagingList(size).setFetchAhead(false).getPage(page - 1);
		} else {
			records = ProductModel.find.where().eq(filter, flavor).orderBy(orderBy + " " + order).findPagingList(size)
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

			msg.data = records.getList();
			msg.page = pageInfo;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + records.getTotalRowCount());
		} else {
			msg.message = NO_FOUND;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + NO_FOUND);
		}
		return ok(Json.toJson(msg));
	}

}
