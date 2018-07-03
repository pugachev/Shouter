package jp.co.shouter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.shouter.bean.ShoutBean;
import jp.co.shouter.form.RegiForm;
import jp.co.shouter.form.ShoutForm;

@Controller
public class TopController {

	private static final Logger logger = LoggerFactory.getLogger(TopController.class);

	@Autowired
    private JdbcTemplate jdbcTemplate;

	@Autowired
	private PlatformTransactionManager txMgr;

	//新投稿書き込み用フォーム(画面→サーバー)
    @ModelAttribute
    public ShoutForm setUpShoutForm()
    {
    	ShoutForm form = new ShoutForm();
        return form;
    }

    //登録用フォーム(画面→サーバー)
    @ModelAttribute
    public RegiForm setUpRegiForm()
    {
    	RegiForm form = new RegiForm();
        return form;
    }

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String login(Locale locale, Model model)
	{
		return "login";
	}

	private static String sha256(String original) throws NoSuchAlgorithmException
	{
        byte[] cipher_byte;
        try
        {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(original.getBytes());
                cipher_byte = md.digest();
                StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
                for(byte b: cipher_byte)
                {
                        sb.append(String.format("%02x", b&0xff) );
                }
                return sb.toString();
        }
        catch (Exception e)
        {
                e.printStackTrace();
        }
        return "";
	}

	//新規登録
	@RequestMapping(value = "/register", method = { RequestMethod.POST,RequestMethod.GET})
	public String register(@Validated @ModelAttribute RegiForm form, BindingResult result, HttpSession session,Model model,RedirectAttributes redirectAttrs) throws NoSuchAlgorithmException
	{
		String rcvPassword = (String)form.getPassword();
		String encodedPassword =  TopController.sha256(rcvPassword);
		System.out.println("rcvPassword="+rcvPassword+" encodedPassword="+encodedPassword);

		String rcvUserName = (String)form.getUsername();
		String rcvSex = (String)form.getSex();
		String rcvProfile = (String)form.getProfile();

		String iconUser="";
		if(rcvSex.equals("male"))
		{
//			iconUser = "icon-user";
			iconUser = "/Shouter/resources/img/male.png";
		}
		else
		{
//			iconUser = "icon-user-female";
			iconUser = "/Shouter/resources/img/female.png";
		}

    	DefaultTransactionDefinition dtDef = new DefaultTransactionDefinition();
    	TransactionStatus tSts = txMgr.getTransaction(dtDef);
		try
		{
//			jdbcTemplate.update("INSERT INTO users (loginId,password,userName,icon,profile,authority) VALUES (?, ?,?,?,?,?)",rcvUserName, rcvPassword,rcvUserName,iconUser,rcvProfile,"ROLE_ADMIN");
			jdbcTemplate.update("INSERT INTO users (loginId,password,userName,icon,profile,authority) VALUES (?, ?,?,?,?,?)",rcvUserName, encodedPassword,rcvUserName,iconUser,rcvProfile,"ROLE_ADMIN");
			txMgr.commit(tSts);
		}
		catch(Exception ex)
		{
			txMgr.rollback(tSts);
			logger.debug("update失敗",ex.toString());
		}

		return "login";
	}

	//トップ(一覧)画面
	@RequestMapping(value = "/top",method = { RequestMethod.POST,RequestMethod.GET})
	public String top(Locale locale, Model model,RedirectAttributes redirectAttributes,RedirectAttributes redirectAttrs)
	{
		//[ログインユーザー情報]を取得
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String username="";
//		String tmp="";
		String profile="";
		String icon="";
		Collection<? extends GrantedAuthority> prinipals= ((UserDetails)principal).getAuthorities();
		if (principal instanceof UserDetails)
		{
		  username = ((UserDetails)principal).getUsername();
//		  tmp = ((UserDetails)principal).getAuthorities().toString();
		}
		else
		{
		  username =  ((UserDetails)principal).getUsername();
		}

		//テーブルusersからログインしたユーザデータを取得する
		String usersSQL = "select * from users where userName = '" + username+ "'";
		List<Map<String, Object>> retUsers = jdbcTemplate.queryForList(usersSQL);

		profile = retUsers.get(0).get("profile").toString();
		icon = retUsers.get(0).get("icon").toString();
//		icon = "/Shouter/resources/img/female.png";
		model.addAttribute("userName", username);
		model.addAttribute("profile", profile);
		model.addAttribute("icon", icon);

		//[みんなの叫び]をテーブルにあるだけ取得(リスト化する)
//	    String shoutsSQL = "select * from shouts where userName = '" + username+ "'";
	    String shoutsSQL = "select * from shouts order by date desc";

		//テーブルshoutsから全データを取得する
		List<Map<String, Object>> retShouts = jdbcTemplate.queryForList(shoutsSQL);
		//画面にわたすデータのリストを生成する
		List<ShoutBean> mList = new ArrayList<ShoutBean>();
		for(int i=0;i<retShouts.size();i++)
		{
			ShoutBean shoutbean = new ShoutBean();
			shoutbean.setShoutsId(retShouts.get(i).get("shoutsId").toString());
			shoutbean.setUserName(retShouts.get(i).get("userName").toString());
			shoutbean.setIcon(retShouts.get(i).get("icon").toString());
			shoutbean.setDate(retShouts.get(i).get("date").toString());
			shoutbean.setWriting(retShouts.get(i).get("writing").toString());
			mList.add(shoutbean);
		}

		model.addAttribute("mList", mList );

		return "top";
	}

	@RequestMapping(value = "/regi", method = RequestMethod.GET)
	public String regi(Locale locale, Model model)
	{
		return "regi";
	}

	//新投稿書き込み画面
	@RequestMapping(value = "/writing", method = RequestMethod.POST)
	public String writing(@Validated @ModelAttribute ShoutForm form, BindingResult result, HttpSession session,Model model,RedirectAttributes redirectAttrs)
	{
		String rcvShout = (String)form.getShout();

		//[ログインユーザー情報]を取得
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String username="";
//		String tmp="";
//		String profile="";
		String icon="";
		Collection<? extends GrantedAuthority> prinipals= ((UserDetails)principal).getAuthorities();
		if (principal instanceof UserDetails)
		{
		  username = ((UserDetails)principal).getUsername();
//		  tmp = ((UserDetails)principal).getAuthorities().toString();
		}
		else
		{
		  username =  ((UserDetails)principal).getUsername();
		}

		//テーブルusersからShoutしたユーザの個人データを取得する
		String usersSQL = "select * from users where userName = '" + username+ "'";
		List<Map<String, Object>> retUsers = jdbcTemplate.queryForList(usersSQL);

//		profile = retUsers.get(0).get("profile").toString();
		icon = retUsers.get(0).get("icon").toString();
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    	DefaultTransactionDefinition dtDef = new DefaultTransactionDefinition();
    	TransactionStatus tSts = txMgr.getTransaction(dtDef);
		try
		{
			jdbcTemplate.update("INSERT INTO shouts (userName,icon,date,writing) VALUES (?, ?,?,?)", username,icon,sdf.format(calender.getTime()),rcvShout);
			txMgr.commit(tSts);
		}
		catch(Exception ex)
		{
			txMgr.rollback(tSts);
			logger.debug("update失敗",ex.toString());
		}

		return "redirect:/top";
	}

	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(Locale locale, Model model) {
		return "error";
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String permission(Locale locale, Model model) {
		return "403";
	}
}
