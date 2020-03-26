package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import LyLib.Utils.Msg;
import LyLib.Utils.PageInfo;
import models.CatalogModel;
import models.ProductModel;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.catalog_backend;

public class CatalogController extends Controller implements IConst {

	@Security.Authenticated(SecuredAdmin.class)
	public static Result backendPage() {
		return ok(catalog_backend.render());
	}

	// @Security.Authenticated(Secured.class)
	public static Result getAllCatalogs(Integer page, Integer size) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		if (size == 0)
			size = PAGE_SIZE;
		if (page <= 0)
			page = 1;

		Msg<List<CatalogModel>> msg = new Msg<>();
		Page<CatalogModel> records;

		records = CatalogModel.find.orderBy("catalogIndex").findPagingList(size).setFetchAhead(false).getPage(page - 1);

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

		// 处理主题下产品数的问题
		ObjectNode resultJson = (ObjectNode) Json.toJson(msg);
		JsonNode dataNode = resultJson.path("data");
		for (int i = 0; i < msg.data.size(); i++) {
			((ObjectNode) dataNode.get(i)).put("productNum", msg.data.get(i).products.size());
		}
		return ok(resultJson);
	}

	// @Security.Authenticated(Secured.class)
	public static Result getCatalog(long id) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<CatalogModel> msg = new Msg<>();

		CatalogModel found = CatalogModel.find.byId(id);
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

	// @Security.Authenticated(Secured.class)
	public static Result getCatalogProductNum(long id) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<Integer> msg = new Msg<>();

		CatalogModel found = CatalogModel.find.byId(id);
		if (found != null) {
			msg.flag = true;
			msg.data = found.products.size();
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + found.products.size());
		} else {
			msg.message = NO_FOUND;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + NO_FOUND);
		}
		return ok(Json.toJson(msg));
	}

	// @Security.Authenticated(Secured.class)
	public static Result getCatalogProducts(long id) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<List<ProductModel>> msg = new Msg<>();

		CatalogModel found = CatalogModel.find.byId(id);
		if (found != null) {
			if (found.products.size() > 0) {
				msg.flag = true;
				msg.data = found.products;
				play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + found.products.size());
			} else {
				msg.message = NO_FOUND;
				play.Logger.info(DateUtil.Date2Str(new Date()) + " - products result: " + NO_FOUND);
			}
		} else {
			msg.message = NO_FOUND;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - catalog result: " + NO_FOUND);
		}
		return ok(Json.toJson(msg));
	}

	@Security.Authenticated(SecuredSuperAdmin.class)
	public static Result addCatalog() {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<CatalogModel> msg = new Msg<>();

		Form<CatalogModel> httpForm = form(CatalogModel.class).bindFromRequest();
		if (!httpForm.hasErrors()) {
			CatalogModel formObj = httpForm.get();
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
	public static Result deleteCatalog(long id) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<CatalogModel> msg = new Msg<>();

		CatalogModel found = CatalogModel.find.byId(id);
		if (found != null) {
			// 解除多对多的关联
			for (ProductModel product : found.products) {
				product.catalogs.remove(found);
				Ebean.update(product);
			}
			found.products = new ArrayList<>();
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
	public static Result updateCatalog(long id) {
		play.Logger.info(DateUtil.Date2Str(new Date()) + " - " + request().method() + ": " + request().uri()
				+ " | DATA: " + request().body().asJson());
		Msg<CatalogModel> msg = new Msg<>();

		CatalogModel found = CatalogModel.find.byId(id);
		if (found == null) {
			msg.message = NO_FOUND;
			play.Logger.info(DateUtil.Date2Str(new Date()) + " - result: " + msg.message);
			return ok(Json.toJson(msg));
		}

		Form<CatalogModel> httpForm = form(CatalogModel.class).bindFromRequest();

		if (!httpForm.hasErrors()) {
			CatalogModel formObj = httpForm.get();

			// 逐个赋值
			found.name = formObj.name;
			found.catalogIndex = formObj.catalogIndex;
			found.images = formObj.images;
			found.smallImages = formObj.smallImages;
			found.wishWord = formObj.wishWord;
			found.description2 = formObj.description2;
			found.description2 = formObj.description2;
			found.comment = formObj.comment;
			// found.products = formObj.products;
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
}
