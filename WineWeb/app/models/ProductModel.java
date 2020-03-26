package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import LyLib.Utils.StrUtil;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name = "products")
public class ProductModel extends Model implements Serializable, IConst {

	@Id
	public Long id;

	public String name; // 名称

	@Lob
	public String images; // 产品图

	@Lob
	public String shortDesc; // 简述

	public String tags; // 标签(多个以","隔开)

	public int alcoholDegree; // 度数

	public int ml; // 容量

	public double resellerMark; // 分销额

	public String flavor; // 风味

	public String productionCity; // 产地

	// public boolean isDefaultTheme; // 是否默认颜色

	public String brand; // 酒的品牌

	public String source; // 原料

	public double totalWeight; // 毛重

	public String productionLicense; // 生产许可证

	public String factoryLocation; // 厂址

	public double price = 0D; // 现价

	public double refPrice = 0D; // 网络参考价

	public double originalPrice = 0D; // 原价

	public boolean isHotSale; // 是否热推

	public int soldNumber; // 卖出数

	public int thumbUp; // 点赞数

	public String previewImage; // 上传的图片

	public String productImage; // 用来做定制预览的图片

	public String bottleSpec; // 酒瓶斤数规格(逗号分隔) 1斤,3斤,5斤,10斤

	public String decoration; // 0打印, 1雕刻
	public boolean canPaint = true; // 可打印(字和图都可打印)
	public boolean canCarve = true; // 可雕刻(不能传图)

	@Lob
	public String description;// 商品介绍(都是图片, 用逗号分隔)

	@Lob
	public String spec;// 规格参数(富文本)

	@Lob
	public String notice;// 购物须知(富文本)

	public String comment;

	public String createdAtStr; // 创建日期字符串表示

	// @Required
	@ManyToMany(targetEntity = models.CatalogModel.class) // , cascade =
															// CascadeType.ALL)
	public List<CatalogModel> catalogs; // 所属主题

	@ManyToMany(targetEntity = models.UserModel.class)
	public List<UserModel> favoriteUsers; // 收藏此商品的用户列表

	@JsonIgnore
	@ManyToMany(targetEntity = models.OrderModel.class)
	public List<OrderModel> orders; // 产品对应的订单

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	public List<ThemeModel> themes; // 颜色主题

	public ProductModel() {
		createdAtStr = DateUtil.Date2Str(new Date());
	}

	// -- Queries

	public static Finder<Long, ProductModel> find = new Finder(Long.class, ProductModel.class);

	/**
	 * Retrieve all .
	 */
	public static List<ProductModel> findAll() {
		return find.all();
	}

	@Override
	public String toString() {
		return "ProductModel [name=" + name + "]";
	}

	public String validate() {
		if (StrUtil.isNull(name)) {
			return "必须要有名称或标题";
		}
		// if (catalogs == null) {
		// return "必须要有所属的主题";
		// }
		// if (StrUtil.isNull(shortDesc)) {
		// return "必须要有产品简介";
		// }
		if (ml == 0) {
			return "必须要有容量信息";
		}
		if (alcoholDegree == 0) {
			return "必须要有度数信息";
		}
		// if (price == 0) {
		// return "必须要有价格信息";
		// }
		return null;
	}
	//
	// public static void delete(Long id) {
	// find.ref(id).delete();
	// }
	//
	// public void setDeleteFlag(boolean flag) {
	// if (deleteFlag != flag) {
	//
	// if (flag == true) {
	// deletedAt = new Date();
	// deletedAtStr = DateUtil.Date2Str(deletedAt);
	// restoredAt = null;
	// restoredAtStr = "";
	// } else {
	// if (deleteFlag == true) { //确保是先删除后还原时才重设时间
	// deletedAt = null;
	// deletedAtStr = "";
	// restoredAt = new Date();
	// restoredAtStr = DateUtil.Date2Str(restoredAt);
	// }
	// }
	// deleteFlag = flag;
	// }
	// }
	//
	// public static void setDeleteFlag(Long id, boolean flag) {
	// ProductModel found = find.ref(id);
	// found.setDeleteFlag(flag);
	// }
	//
	// public void setActiveFlag(boolean flag) {
	// if (activeFlag != flag) {
	//
	// if (flag == true) {
	// activedAt = new Date();
	// activedAtStr = DateUtil.Date2Str(activedAt);
	// deactivedAt = null;
	// deactivedAtStr = "";
	// } else {
	// activedAt = null;
	// activedAtStr = "";
	// deactivedAt = new Date();
	// deactivedAtStr = DateUtil.Date2Str(deactivedAt);
	// }
	// activeFlag = flag;
	// }
	// }
	//
	// public static void setActiveFlag(Long id, boolean flag) {
	// ProductModel found = find.ref(id);
	// found.setActiveFlag(flag);
	// }
	//
	// public static void updateContentImage(Long content, String imgName,
	// Boolean isBig) {
	// ProductModel found = find.byId(content);
	// if (found != null) {
	// if (isBig) {
	// found.bigPic = imgName;
	// } else {
	// found.smallPic = imgName;
	// }
	// found.update();
	// }
	// }
	//
	// public static void saveOrUpdate(ProductModel cm) {
	// ProductModel found = find.byId(cm.id);
	// if (found != null) {
	// cm.update();
	// } else {
	// cm.save();
	// }
	// }
}
