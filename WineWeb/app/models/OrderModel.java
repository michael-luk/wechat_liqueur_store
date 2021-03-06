package models;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import LyLib.Utils.StrUtil;
import jdk.nashorn.internal.ir.annotations.Reference;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderModel extends Model implements IConst {

	@Id
	public Long id;

	public String orderNo; // 订单号

	public Long refResellerId = 0l;// 分销用户ID

	public UserModel reseller; // 分销用户

	public Long refBuyerId = 0l;// 用户ID

	@ManyToOne
	public UserModel buyer; // 购买用户

	@ManyToMany(targetEntity = models.ProductModel.class)
	public List<ProductModel> orderProducts; // 订单对应的产品

	public Long refThemeId;// 产品风格ID

	public String refThemeName;// 产品风格名称

	public String wishWord;// 祝福语

	public String wishImage;// 祝福图片

	public double price;// 单价

	public int wineWeight; // 斤数

	public String decoration; // 工艺

	public String wineBody; // 酒体

	public int quantity;// 数量

	public double shipFee;// 运费

	public double productAmount; // 商品总额

	public double amount; // 订单总额(含运费)

	public int jifen; // 使用积分

	public double jifenAmount; // 积分抵扣金额

	public double promotionAmount; // 优惠金额

	public String invoiceTitle;// 发票抬头(用户若选个人, 抬头为"个人", 若选单位, 抬头为单位名称)

	public int status; // 状态: 0新建, 1已支付, 2已取消, 3已删除, 4已发货, 5已确认, 6已计算佣金,
						// 7已取消计算佣金, 8支付失败, 9等待支付结果

	public String shipName; // 名称

	public String shipPhone; // 联系电话

	public String shipPostCode; // 邮编

	public String shipProvice; // 省

	public String shipCity; // 市

	public String shipZone; // 区

	public String shipLocation; // 地址

	public String logisticsCompany; // 物流公司

	public String numberOfLogistics; // 物流单号

	public String shipTimeStr; // 发货时间 日期字符串表示

	public String createClientIP; // 创建订单时候我们后台记录的客户的IP
	public String createClientarea; // 支付执行时候我们后台记录的客户的IP所在区域*

	public String payClientIP; // 支付执行时候我们后台记录的客户的IP
	public String payClientarea; // 支付执行时候我们后台记录的客户的IP所在区域*

	public String payReturnCode; // 支付执行后第三方的返回码SUCCESS/FAIL
	public String payReturnMsg; // 支付执行后第三方的返回消息(return code=FAIL才显示)
	public String payResultCode; // 支付执行后第三方的业务结果SUCCESS/FAIL
	public String payTransitionId; // 支付执行后第三方的流水ID
	public String payAmount; // 支付执行后第三方的支付实际金额
	public String payBank; // 支付执行后第三方的支付银行
	public String payRefOrderNo; // 支付执行后第三方返回的我们的订单号(不是ID, 是orderNo)
	public String paySign; // 支付执行后第三方的返回的签名**
	public String payTime; // 支付执行后第三方的支付时间
	public String payThirdPartyId; // 支付执行后第三方的用户ID(如微信openId)
	public String payThirdPartyUnionId; // 支付执行后第三方的全局用户ID(如微信unionId)**

	// public String payIP; // 支付执行时候客户的IP
	// public String payIParea; // 支付执行时候客户的IP所在区域

	public double resellerProfit1; // 第1级佣金
	public double resellerProfit2; // 第2级佣金
	public double resellerProfit3; // 第3级佣金

	@JsonIgnore
	@ManyToMany(targetEntity = models.PromotionModel.class)
	public List<PromotionModel> promotions; // 订单使用的优惠(可能多个)

	public String comment;

	public String createdAtStr; // 创建日期字符串表示
	public String doResellerStr; // 执行分销日期字符串表示

	public OrderModel() {
		createdAtStr = DateUtil.Date2Str(new Date());
	}

	public static Finder<Long, OrderModel> find = new Finder(Long.class, OrderModel.class);

	public static List<OrderModel> findAll() {
		return find.all();
	}

	@Override
	public String toString() {
		return "Order [No:" + orderNo + "]";
	}
	//
	// public String validate() {
	// if (StrUtil.isNull(refThemeName)) {
	// return "必须要有产品风格名称";
	// }
	// return null;
	// }
}
