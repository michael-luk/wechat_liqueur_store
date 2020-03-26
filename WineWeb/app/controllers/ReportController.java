package controllers;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import LyLib.Utils.Msg;
import LyLib.Utils.StrUtil;
import com.avaje.ebean.Ebean;
import models.*;
import models.common.CompanyModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.Region;

import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static play.data.Form.form;

public class ReportController extends Controller implements IConst {

	public static Result exportOrderReport(boolean all) {
		String fileName = DateUtil.NowString("yyyy_MM_dd_HH_mm_ss");//

		// 创建工作薄对象
		HSSFWorkbook workbook2007 = new HSSFWorkbook();
		// 把订单表放进集合
		List<OrderModel> order;
		if (all) {
			fileName += "_ALL.xls";
			order = OrderModel.find.orderBy("id desc").findList();

		} else {
			fileName += ".xls";
			order = OrderModel.find.where().eq("status", 1).orderBy("id desc").findList();
		}

		// 创建单元格样式
		HSSFCellStyle cellStyle = workbook2007.createCellStyle();
		// 设置边框属性
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// 指定单元格居中对齐
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 指定单元格垂直居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 指定当单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);
		// // 设置单元格字体
		HSSFFont font = workbook2007.createFont();
		font.setFontName("宋体");
		// 大小
		font.setFontHeightInPoints((short) 10);
		// 加粗
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellStyle.setFont(font);

		HSSFCellStyle style = workbook2007.createCellStyle();
		// 指定单元格居中对齐
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 指定单元格垂直居中对齐
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont font1 = workbook2007.createFont();
		font1.setFontName("宋体");
		font1.setFontHeightInPoints((short) 10);
		// 加粗
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font1);

		// for (OrderModel module : order) {
		// List<RegInfo> regInfoList = RegInfo.find.where().eq("module_id",
		// module.id).findList();

		// List<OrderModel> order2 = new ArrayList<OrderModel>();
		// for (RegInfo regInfo : regInfoList) {
		// users.add(regInfo.user);
		// }

		// 创建工作表对象，并命名
		HSSFSheet sheet2 = workbook2007.createSheet("订单列表");
		// 设置列宽
		sheet2.setColumnWidth(0, 1500);
		sheet2.setColumnWidth(1, 1500);
		sheet2.setColumnWidth(2, 850);
		sheet2.setColumnWidth(3, 3000);
		sheet2.setColumnWidth(4, 850);
		sheet2.setColumnWidth(5, 1950);
		sheet2.setColumnWidth(7, 1300);
		sheet2.setColumnWidth(8, 1300);
		sheet2.setColumnWidth(9, 1300);
		sheet2.setColumnWidth(10, 1800);
		sheet2.setColumnWidth(11, 1900);
		sheet2.setColumnWidth(12, 1800);
		sheet2.setColumnWidth(13, 3400);
		sheet2.setColumnWidth(14, 1800);
		sheet2.setColumnWidth(15, 3650);

		sheet2.setDefaultColumnStyle(0, cellStyle);
		sheet2.setDefaultColumnStyle(1, cellStyle);
		sheet2.setDefaultColumnStyle(2, cellStyle);
		sheet2.setDefaultColumnStyle(3, cellStyle);
		sheet2.setDefaultColumnStyle(4, cellStyle);
		sheet2.setDefaultColumnStyle(5, cellStyle);
		sheet2.setDefaultColumnStyle(6, cellStyle);
		sheet2.setDefaultColumnStyle(7, cellStyle);
		sheet2.setDefaultColumnStyle(8, cellStyle);
		sheet2.setDefaultColumnStyle(9, cellStyle);
		sheet2.setDefaultColumnStyle(10, cellStyle);
		sheet2.setDefaultColumnStyle(11, cellStyle);
		sheet2.setDefaultColumnStyle(12, cellStyle);
		sheet2.setDefaultColumnStyle(13, cellStyle);
		sheet2.setDefaultColumnStyle(14, cellStyle);
		sheet2.setDefaultColumnStyle(15, cellStyle);

		// 创建表头
		HSSFRow title = sheet2.createRow(0);
		title.setHeightInPoints(50);
		title.createCell(0).setCellValue("                    酒业（龙鑫浩然定制酒）发货单              茅台镇");
		title.createCell(1).setCellValue("");
		title.createCell(2).setCellValue("");
		title.createCell(3).setCellValue("");
		title.createCell(4).setCellValue("");
		title.createCell(5).setCellValue("");
		title.createCell(6).setCellValue("");
		title.createCell(7).setCellValue("");
		title.createCell(8).setCellValue("");
		title.createCell(9).setCellValue("");
		title.createCell(10).setCellValue("");
		title.createCell(11).setCellValue("");
		title.createCell(12).setCellValue("");
		title.createCell(13).setCellValue("");
		title.createCell(14).setCellValue("");
		title.createCell(15).setCellValue("");
		sheet2.addMergedRegion(new Region(0, (short) 0, 0, (short) 15));
		HSSFCell ce = title.createCell((short) 1);

		HSSFRow titleRow = sheet2.createRow(1);
		// titleRow.setRowStyle(cellStyle);
		// 设置行高
		titleRow.setHeightInPoints(30);
		titleRow.createCell(0).setCellValue("姓名");
		titleRow.createCell(1).setCellValue("订单号");
		titleRow.createCell(2).setCellValue("状态");
		titleRow.createCell(3).setCellValue("产品名称");
		titleRow.createCell(4).setCellValue("工艺");
		titleRow.createCell(5).setCellValue("备注");
		titleRow.createCell(6).setCellValue("祝福语");
		titleRow.createCell(7).setCellValue("酒体");
		titleRow.createCell(8).setCellValue("规格 ");
		titleRow.createCell(9).setCellValue("数量");
		titleRow.createCell(10).setCellValue("酒价");
		titleRow.createCell(11).setCellValue("总合计：");
		titleRow.createCell(12).setCellValue("收货人");
		titleRow.createCell(13).setCellValue("电话");
		titleRow.createCell(14).setCellValue("省份");
		titleRow.createCell(15).setCellValue("地址");
		HSSFCell ce2 = title.createCell((short) 2);
		ce2.setCellStyle(cellStyle); // 样式，居中

		// 遍历集合对象创建行和单元格
		for (int i = 0; i < order.size(); i++) {
			// 取出对象
			OrderModel order2 = order.get(i);
			// 创建行
			HSSFRow row = sheet2.createRow(i + 2);
			// 创建单元格并赋值

			// 用户姓名
			HSSFCell nameCell = row.createCell(0);
			if (order2.buyer == null) {
				nameCell.setCellValue("无");
			} else {
				if ("".equals(order2.buyer.nickname)) {
					nameCell.setCellValue("无");
				}
				nameCell.setCellValue(order2.buyer.nickname);
			}

			// 订单号
			HSSFCell orderNoCell = row.createCell(1);
			orderNoCell.setCellValue(order2.orderNo);

			// HSSFCell statusCell = row.createCell(2);
			// if (order2.status == 1) {
			// statusCell.setCellValue(order2.status);
			// statusCell.setCellValue("已支付");
			// }

			// 支付状态
			HSSFCell statusCell = row.createCell(2);

			if (order2.status == 1) {
				statusCell.setCellValue("已支付");
			} else if (order2.status == 2) {
				statusCell.setCellValue("已取消");
			} else if (order2.status == 3) {
				statusCell.setCellValue("已删除");
			} else if (order2.status == 4) {
				statusCell.setCellValue("已发货");
			} else if (order2.status == 5) {
				statusCell.setCellValue("已确认");
			} else if (order2.status == 6) {
				statusCell.setCellValue("已计算佣金");
			} else if (order2.status == 7) {
				statusCell.setCellValue("已取消计算佣金");
			} else if (order2.status == 8) {
				statusCell.setCellValue("支付失败");
			} else if (order2.status == 9) {
				statusCell.setCellValue("等待支付结果");
			}
			// 产品名称
			HSSFCell productCell = row.createCell(3);

			if (order2.orderProducts != null && order2.orderProducts.size() > 0) {
				productCell.setCellValue(order2.orderProducts.get(0).name);
			} else {
				productCell.setCellValue("无");
			}

			// 工艺
			HSSFCell gongyiCell = row.createCell(4);
			gongyiCell.setCellValue(order2.decoration);
			// 颜色
			HSSFCell colorCell = row.createCell(5);

			if (order2.orderProducts != null && order2.orderProducts.size() > 0) {
				colorCell.setCellValue(order2.orderProducts.get(0).comment);
			} else {
				colorCell.setCellValue("无");
			}
			// 祝福语
			HSSFCell wishWordCell = row.createCell(6);
			wishWordCell.setCellValue(order2.wishWord);
			// 酒体
			HSSFCell wineBodyCell = row.createCell(7);
			wineBodyCell.setCellValue(order2.wineBody);
			// 规格
			HSSFCell wineWeightCell = row.createCell(8);
			wineWeightCell.setCellValue(order2.wineWeight);
			// 数量
			HSSFCell quantityCell = row.createCell(9);
			quantityCell.setCellValue(order2.quantity);
			// 单价
			HSSFCell priceCell = row.createCell(10);
			priceCell.setCellValue(order2.price);
			// 总价
			HSSFCell amountCell = row.createCell(11);
			amountCell.setCellValue(order2.amount);
			// 收货人姓名
			HSSFCell shipNameCell = row.createCell(12);
			shipNameCell.setCellValue(order2.shipName);
			// 电话
			HSSFCell shipPhoneCell = row.createCell(13);
			shipPhoneCell.setCellValue(order2.shipPhone);
			// 省份
			HSSFCell shipProviceCell = row.createCell(14);
			shipProviceCell.setCellValue(order2.shipProvice);
			// 地址
			HSSFCell shipLocationCell = row.createCell(15);
			shipLocationCell.setCellValue(order2.shipLocation);

		}

		// 生成文件
		String path = Play.application().path().getPath() + "/public/report/" + fileName;
		File file = new File(path);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			workbook2007.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// response().setHeader("Content-Disposition", "attachment; filename=" +
		// fileName);
		return ok(file);
	}

	public static Result exportProductReport() {
		String fileName = DateUtil.NowString("yyyy_MM_dd_HH_mm_ss");//

		// 创建工作薄对象
		HSSFWorkbook workbook2007 = new HSSFWorkbook();
		// 把订单表放进集合
		fileName += ".xls";
		List<ProductModel> product = ProductModel.find.orderBy("id desc").findList();
		// 创建单元格样式
		HSSFCellStyle cellStyle = workbook2007.createCellStyle();
		// 设置边框属性
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// 指定单元格居中对齐
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 指定单元格垂直居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 指定当单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);
		// // 设置单元格字体
		HSSFFont font = workbook2007.createFont();
		font.setFontName("宋体");
		// 大小
		font.setFontHeightInPoints((short) 10);
		// 加粗
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellStyle.setFont(font);

		HSSFCellStyle style = workbook2007.createCellStyle();
		// 指定单元格居中对齐
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 指定单元格垂直居中对齐
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont font1 = workbook2007.createFont();
		font1.setFontName("宋体");
		font1.setFontHeightInPoints((short) 10);
		// 加粗
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font1);

		// for (OrderModel module : order) {
		// List<RegInfo> regInfoList = RegInfo.find.where().eq("module_id",
		// module.id).findList();

		// List<OrderModel> order2 = new ArrayList<OrderModel>();
		// for (RegInfo regInfo : regInfoList) {
		// users.add(regInfo.user);
		// }

		// 创建工作表对象，并命名
		HSSFSheet sheet2 = workbook2007.createSheet("产品详情");
		// 设置列宽
		sheet2.setColumnWidth(2, 5500);
		sheet2.setColumnWidth(5, 2800);
		sheet2.setColumnWidth(6, 4500);

		sheet2.setDefaultColumnStyle(0, cellStyle);
		sheet2.setDefaultColumnStyle(1, cellStyle);
		sheet2.setDefaultColumnStyle(2, cellStyle);
		sheet2.setDefaultColumnStyle(3, cellStyle);
		sheet2.setDefaultColumnStyle(4, cellStyle);
		sheet2.setDefaultColumnStyle(5, cellStyle);
		sheet2.setDefaultColumnStyle(6, cellStyle);

		// 创建表头
		HSSFRow title = sheet2.createRow(0);
		title.setHeightInPoints(50);
		title.createCell(0).setCellValue("酒业（龙鑫浩然定制酒）产品");
		title.createCell(1).setCellValue("");
		title.createCell(2).setCellValue("");
		title.createCell(3).setCellValue("");
		title.createCell(4).setCellValue("");
		title.createCell(5).setCellValue("");
		title.createCell(6).setCellValue("");
		sheet2.addMergedRegion(new Region(0, (short) 0, 0, (short) 6));
		HSSFCell ce = title.createCell((short) 1);

		HSSFRow titleRow = sheet2.createRow(1);
		// titleRow.setRowStyle(cellStyle);
		// 设置行高
		titleRow.setHeightInPoints(30);
		titleRow.createCell(0).setCellValue("Id");
		titleRow.createCell(1).setCellValue("所属主题");
		titleRow.createCell(2).setCellValue("名称");
		titleRow.createCell(3).setCellValue("酒精度数");
		titleRow.createCell(4).setCellValue("容量");
		titleRow.createCell(5).setCellValue("酒瓶数规格");
		titleRow.createCell(6).setCellValue("备注");

		// 遍历集合对象创建行和单元格
		for (int i = 0; i < product.size(); i++) {
			// 取出对象
			ProductModel product2 = product.get(i);
			// 创建行
			HSSFRow row = sheet2.createRow(i + 2);
			// 创建单元格并赋值
			// 产品Id
			// 名称
			HSSFCell IdCell = row.createCell(0);
			IdCell.setCellValue(product2.id);
			// 所属主题
			HSSFCell productCell = row.createCell(1);
			if (product2.catalogs != null && product2.catalogs.size() > 0) {
				productCell.setCellValue(product2.catalogs.get(0).name);
			} else {
				productCell.setCellValue("无");
			}
			// 名称
			HSSFCell nameCell = row.createCell(2);
			nameCell.setCellValue(product2.name);
			// 酒精度数
			HSSFCell alcoholDegreeCell = row.createCell(3);
			alcoholDegreeCell.setCellValue(product2.alcoholDegree);
			// 容量
			HSSFCell mlCell = row.createCell(4);
			mlCell.setCellValue(product2.ml);
			// 酒瓶斤数规格
			HSSFCell bottleSpecCell = row.createCell(5);
			bottleSpecCell.setCellValue(product2.bottleSpec);
			// 备注
			HSSFCell commentCell = row.createCell(6);
			commentCell.setCellValue(product2.comment);

		}

		// 生成文件
		String path = Play.application().path().getPath() + "/public/report/" + fileName;
		File file = new File(path);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			workbook2007.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// response().setHeader("Content-Disposition", "attachment; filename=" +
		// fileName);
		return ok(file);
	}
}