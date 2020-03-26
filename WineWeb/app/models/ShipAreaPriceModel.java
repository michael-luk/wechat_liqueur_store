package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import LyLib.Utils.StrUtil;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name = "shipareaprices")
public class ShipAreaPriceModel extends Model implements Serializable, IConst {

	@Id
	public Long id;

	public String area; // 地区

	public double shipPrice; // 单位运费

	public String comment;

	public static Finder<Long, ShipAreaPriceModel> find = new Finder(Long.class, ShipAreaPriceModel.class);

	/**
	 * Retrieve all .
	 */
	public static List<ShipAreaPriceModel> findAll() {
		return find.all();
	}

	public String validate() {
		if (StrUtil.isNull(area)) {
			return "必须要有区域名称";
		}
		if (shipPrice == 0) {
			return "必须要有单位运费";
		}
		return null;
	}

	@Override
	public String toString() {
		return "ShipAreaPrice [area:" + area + "]";
	}
}