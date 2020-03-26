import java.util.Date;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;

import LyLib.Interfaces.IConst;
import LyLib.Utils.DateUtil;
import controllers.WeiXinController;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import models.CatalogModel;
import models.FondOutRequestModel;
import models.InfoModel;
import models.OrderModel;
import models.ProductModel;
import models.PromotionModel;
import models.ShipAreaPriceModel;
import models.ShipInfoModel;
import models.ThemeModel;
import models.UserModel;
import models.WineBodyModel;
import models.common.CompanyModel;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

/**
 * Created by yanglu on 14/11/14.
 */
public class Global extends GlobalSettings implements IConst {

    public void onStart(Application app) {
        play.Logger.info(SYSTEM_LAUNCH_INFO);
        InitialData.insert(app);
        WeiXinController.wxInit();
    }

    static class InitialData {
        public static void insert(Application app) {
            if (Ebean.find(UserModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("users");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);
                            List<UserModel> list = UserModel.findAll();
                            for (UserModel user : list) {
                                user.resellerCode = UserModel.generateResellerCode();
                                try {
                                    user.resellerCodeImage = WeiXinController.generateResellerCodeBarcode(user.resellerCode);
                                } catch (Exception e) {
                                    play.Logger.error(DateUtil.Date2Str(new Date())
                                            + " - error on create reseller barcode: " + e.getMessage());
                                    e.printStackTrace();
                                }
                                Ebean.update(user);
                            }
                            play.Logger.info(String.format("load cfg default users %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default users done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(ProductModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("products");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);
                            play.Logger.info(String.format("load cfg default products %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default products done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(WineBodyModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("winebodys");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);
                            play.Logger.info(String.format("load cfg default winebodys %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default shipinfos done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(PromotionModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("promotions");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);
                            play.Logger.info(String.format("load cfg default promotions %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default promotions done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(ShipAreaPriceModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("shipareaprices");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);
                            play.Logger.info(String.format("load cfg default shipareaprices %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default shipareaprices done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(ShipInfoModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("shipinfos");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);

                            List<UserModel> users = UserModel.findAll();

                            List<ShipInfoModel> shipinfos = ShipInfoModel.findAll();

                            if (users.size() > 0 && shipinfos.size() > 0) {

                                // shipinfo -> user
                                users.get(1).shipInfos.add(shipinfos.get(0));
                                shipinfos.get(0).refUserId = users.get(1).id;
                                shipinfos.get(0).user = users.get(1);

                                users.get(1).shipInfos.add(shipinfos.get(1));
                                shipinfos.get(1).refUserId = users.get(1).id;
                                shipinfos.get(1).user = users.get(1);

                                users.get(1).shipInfos.add(shipinfos.get(2));
                                shipinfos.get(2).refUserId = users.get(1).id;
                                shipinfos.get(2).user = users.get(1);

                                for (UserModel user : users) {
                                    Ebean.update(user);
                                }
                            }

                            play.Logger.info(String.format("load cfg default shipinfos %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default shipinfos done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(CompanyModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("companys");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);
                            play.Logger.info(String.format("load cfg default company %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default company done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(CatalogModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("catalogs");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);
                            play.Logger.info(String.format("load cfg default catalogs %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default catalogs done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(ThemeModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("themes");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);

                            List<CatalogModel> catalogs = CatalogModel.findAll();

                            List<ProductModel> products = ProductModel.findAll();

                            List<ThemeModel> themes = ThemeModel.findAll();

                            List<UserModel> users = UserModel.findAll();

                            if (catalogs.size() > 0 && products.size() > 0 && themes.size() > 0) {

                                // catalog -> product
                                products.get(0).catalogs.add(catalogs.get(0));
                                products.get(1).catalogs.add(catalogs.get(0));
                                products.get(1).catalogs.add(catalogs.get(5));
                                products.get(2).catalogs.add(catalogs.get(6));
                                products.get(3).catalogs.add(catalogs.get(7));

                                // product -> catalog
                                catalogs.get(0).products.add(products.get(0));
                                catalogs.get(0).products.add(products.get(1));
                                catalogs.get(5).products.add(products.get(1));
                                catalogs.get(6).products.add(products.get(2));
                                catalogs.get(7).products.add(products.get(3));

                                // user -> product
                                products.get(0).favoriteUsers.add(users.get(1));
                                products.get(1).favoriteUsers.add(users.get(1));
                                products.get(2).favoriteUsers.add(users.get(2));
                                products.get(3).favoriteUsers.add(users.get(2));

                                // product -> user
                                users.get(1).favoriteProducts.add(products.get(0));
                                users.get(1).favoriteProducts.add(products.get(1));
                                users.get(2).favoriteProducts.add(products.get(2));
                                users.get(2).favoriteProducts.add(products.get(3));

                                // theme -> product
                                products.get(0).themes.add(themes.get(0));
                                themes.get(0).refProductId = products.get(0).id;
                                themes.get(0).product = products.get(0);
                                products.get(0).themes.add(themes.get(1));
                                themes.get(1).refProductId = products.get(0).id;
                                themes.get(1).product = products.get(0);

                                products.get(1).themes.add(themes.get(2));
                                themes.get(2).refProductId = products.get(1).id;
                                themes.get(2).product = products.get(1);
                                products.get(1).themes.add(themes.get(3));
                                themes.get(3).refProductId = products.get(1).id;
                                themes.get(3).product = products.get(1);

                                products.get(2).themes.add(themes.get(4));
                                themes.get(4).refProductId = products.get(2).id;
                                themes.get(4).product = products.get(2);
                                products.get(2).themes.add(themes.get(5));
                                themes.get(5).refProductId = products.get(2).id;
                                themes.get(5).product = products.get(2);

                                products.get(3).themes.add(themes.get(6));
                                themes.get(6).refProductId = products.get(3).id;
                                themes.get(6).product = products.get(3);
                                products.get(3).themes.add(themes.get(7));
                                themes.get(7).refProductId = products.get(3).id;
                                themes.get(7).product = products.get(3);

                                for (ProductModel product : products) {
                                    Ebean.update(product);
                                }

                                Ebean.update(products.get(0));
                                Ebean.update(products.get(1));
                                Ebean.update(products.get(2));
                                Ebean.update(products.get(3));

                                Ebean.update(catalogs.get(0));
                                Ebean.update(catalogs.get(5));
                                Ebean.update(catalogs.get(6));
                                Ebean.update(catalogs.get(7));

                                Ebean.update(users.get(1));
                                Ebean.update(users.get(2));
                            }
                            play.Logger.info(String.format("load cfg default themes %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default themes done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.toString());
                }
            }
            if (Ebean.find(OrderModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("orders");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);

                            List<ProductModel> products = ProductModel.findAll();

                            List<UserModel> users = UserModel.findAll();

                            List<OrderModel> orders = OrderModel.findAll();

                            if (products.size() > 0 && users.size() > 0 && orders.size() > 0) {

                                // order -> product
                                products.get(0).orders.add(orders.get(0));
                                products.get(1).orders.add(orders.get(1));

                                // product -> order
                                orders.get(0).orderProducts.add(products.get(0));
                                orders.get(1).orderProducts.add(products.get(1));

                                // order -> user
                                users.get(1).orders.add(orders.get(0));
                                orders.get(0).refBuyerId = users.get(1).id;
                                orders.get(0).buyer = users.get(1);
                                orders.get(0).refResellerId = users.get(1).id;

                                users.get(1).orders.add(orders.get(1));
                                orders.get(1).refBuyerId = users.get(1).id;
                                orders.get(1).buyer = users.get(1);
                                orders.get(1).refResellerId = users.get(1).id;

                                for (UserModel user : users) {
                                    Ebean.update(user);
                                }

                                Ebean.update(products.get(0));
                                Ebean.update(products.get(1));

                                Ebean.update(orders.get(0));
                                Ebean.update(orders.get(1));

                            }

                            play.Logger.info(String.format("load cfg default orders %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default orders done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(InfoModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("infos");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);
                            play.Logger.info(String.format("load cfg default infos %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default infos done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
            if (Ebean.find(FondOutRequestModel.class).findRowCount() == 0) {
                try {
                    Map<String, List<Object>> initData = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
                    List<Object> defaultObjs = initData.get("fondoutrequests");
                    if (defaultObjs != null) {
                        if (defaultObjs.size() > 0) {
                            Ebean.save(defaultObjs);
                            play.Logger.info(String.format("load cfg default fondoutrequests %s", defaultObjs.size()));
                        }
                    }
                    play.Logger.info("load cfg default fondoutrequests done");
                } catch (Exception ex) {
                    play.Logger.error(CONFIG_FILE_ISSUE + ": " + ex.getMessage());
                }
            }
        }
    }
}
