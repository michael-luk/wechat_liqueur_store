package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import LyLib.Interfaces.IConst;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name = "promotions")
public class PromotionModel extends Model implements Serializable, IConst {

	@Id
	public Long id;

	public String classify; // 类型 满减, 免运费, 首单, 优惠码

	public double reachMoney; // 满足金额

	public double discount; // 减少金额

	public String comment;

	@JsonIgnore
	@ManyToMany(targetEntity = models.OrderModel.class)
	public List<OrderModel> orders;

	public static Finder<Long, PromotionModel> find = new Finder(Long.class, PromotionModel.class);

	public static List<PromotionModel> findAll() {
		return find.all();
	}

	@Override
	public String toString() {
		return "Promotion [classify:" + classify + "]";
	}

}
