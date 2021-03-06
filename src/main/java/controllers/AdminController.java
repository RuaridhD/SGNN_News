package controllers;

import db.DBHelper;
import models.Article;
import models.CategoryType;
import models.Journalist;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;


public class AdminController{

        public AdminController(){
        this.setupEndPoints();
        }

    private void setupEndPoints() {

        //        ADMIN INDEX
        get("/admin", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Article> articles = DBHelper.orderByPageViewsMostFirst();
            String loggedInUser = LoginController.getLoggedInUserName(req, res);
            model.put("user", loggedInUser);
            model.put("template", "templates/admin/index.vtl");
            model.put("articles", articles);
            return new ModelAndView(model, "templates/adminlayout.vtl");
        }, new VelocityTemplateEngine());

        //        ARTICLE INDEX
        get("/admin/articles", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Article> articles = DBHelper.orderByDateCreatedNewestFirst();
            String loggedInUser = LoginController.getLoggedInUserName(req, res);
            model.put("user", loggedInUser);
            model.put("template", "templates/admin/article.vtl");
            model.put("articles", articles);
            return new ModelAndView(model, "templates/adminlayout.vtl");
        }, new VelocityTemplateEngine());


        //          NEW ARTICLE
        get("/admin/articles/new", (req, res) -> {
            List<Journalist> journalists = DBHelper.getAll(Journalist.class);
            HashMap<String, Object> model = new HashMap<>();
            Set<CategoryType> categories = EnumSet.allOf(CategoryType.class);
            String loggedInUser = LoginController.getLoggedInUserName(req, res);
            model.put("user", loggedInUser);
            model.put("categories", categories);
            model.put("journalists", journalists);
            model.put("template", "templates/admin/create_article.vtl");
            return new ModelAndView(model, "templates/adminlayout.vtl");
        }, new VelocityTemplateEngine());

        //        CREATE ARTICLE
        post("/admin/articles", (req, res) -> {
            int journalistId = Integer.parseInt(req.queryParams("journalist"));
            Journalist journalist = DBHelper.find(journalistId, Journalist.class);
            String headline = req.queryParams("headline");
            String summary = req.queryParams("summary");
            String story = req.queryParams("story");
            CategoryType category = CategoryType.valueOf(req.queryParams("category"));
            String heroImage = req.queryParams(("heroImage"));
            String articleImage = req.queryParams(("articleImage"));

            Article newArticle = new Article(journalist, headline, summary, story, category, heroImage, articleImage);

            DBHelper.save(newArticle);
            res.redirect("/admin/articles");
            return null;
        });

        //        EDIT ARTICLE
        get("/admin/articles/:id/edit", (req, res) -> {
            String stringId = req.params(":id");
            Integer intId = Integer.parseInt(stringId);
            Article article = DBHelper.find(intId, Article.class);
            List<Journalist> journalists = DBHelper.getAll(Journalist.class);
            Set<CategoryType> categories = EnumSet.allOf(CategoryType.class);
            Map<String, Object> model = new HashMap<>();
            String loggedInUser = LoginController.getLoggedInUserName(req, res);
            model.put("categories", categories);
            model.put("user", loggedInUser);
            model.put("journalists", journalists);
            model.put("template", "templates/admin/edit_article.vtl");
            model.put("article", article);
            return new ModelAndView(model, "templates/adminlayout.vtl");
        }, new VelocityTemplateEngine());


        //        UPDATE ARTICLE
        post("/admin/articles/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            Article article = DBHelper.find(id, Article.class);
            int journalistId = Integer.parseInt(req.queryParams("journalist"));
            Journalist journalist = DBHelper.find(journalistId, Journalist.class);
            article.setHeadline(req.queryParams("headline"));
            article.setSummary(req.queryParams("summary"));
            article.setStory(req.queryParams("story"));
            article.setJournalist(journalist);
            CategoryType category = CategoryType.valueOf(req.queryParams("category"));
            article.setCategory(category);
            article.setHeroImage(req.queryParams("heroImage"));
            article.setArticleImage(req.queryParams("articleImage"));

            DBHelper.save(article);
            res.redirect("/admin/articles");
            return null;
        });


        //        DELETE ARTICLE
        post("/admin/articles/:id/delete", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            Article article = DBHelper.find(id, Article.class);
            DBHelper.delete(article);
            res.redirect("/admin/articles");
            return null;
        });


        //        JOURNALIST INDEX
        get("/admin/journalists", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Journalist> journalists = DBHelper.getAll(Journalist.class);
            String loggedInUser = LoginController.getLoggedInUserName(req, res);
            model.put("user", loggedInUser);
            model.put("template", "templates/admin/journalist.vtl");
            model.put("journalists", journalists);
            return new ModelAndView(model, "templates/adminlayout.vtl");
        }, new VelocityTemplateEngine());


        //        CREATE JOURNALIST
        post("/admin/journalists", (req, res) -> {
            String name = req.queryParams("name");
            Journalist journalist = new Journalist(name);
            DBHelper.save(journalist);
            res.redirect("/admin/journalists");
            return null;
        }, new VelocityTemplateEngine());


        //        EDIT JOURNALIST
        get("/admin/journalists/:id/edit", (req, res) -> {
            String strId = req.params(":id");
            Integer intId = Integer.parseInt(strId);
            Journalist journalist = DBHelper.find(intId, Journalist.class);

            Map<String, Object> model = new HashMap<>();
            String loggedInUser = LoginController.getLoggedInUserName(req, res);
            model.put("user", loggedInUser);
            model.put("journalist", journalist);
            model.put("template", "templates/admin/edit_journalist.vtl");

            return new ModelAndView(model, "templates/adminlayout.vtl");
        }, new VelocityTemplateEngine());


        //        UPDATE JOURNALIST
        post("/admin/journalists/:id", (req, res) -> {
            String strId = req.params(":id");
            Integer intId = Integer.parseInt(strId);
            Journalist journalist = DBHelper.find(intId, Journalist.class);
            String name = req.queryParams("name");
            journalist.setName(name);
            DBHelper.save(journalist);
            res.redirect("/admin/journalists");
            return null;

        }, new VelocityTemplateEngine());


        //        DELETE JOURNALIST
        post("/admin/journalists/:id/delete", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Journalist journalistToDelete = DBHelper.find(id, Journalist.class);
            DBHelper.delete(journalistToDelete);
            res.redirect("/admin/journalists");
            return null;
        }, new VelocityTemplateEngine());




    }
}
