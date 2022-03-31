package com.example.demo.member;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.common.FileUploadUtil;

@CrossOrigin("*")
@RestController
public class MemberController {
	@Value("${fileUploadPath}") //src/main/resources/application.properies�� ���� �о�´�
	String fileUploadPath;
	
	@Value("${domain}")
	String domain;
	
	@Resource(name="memberService")
	MemberService memberService;
	
//	@RequestMapping(value="/member/login")
//	public String member_login()
//	{
//		return "member/member_login";
//	}
	
	@RequestMapping("/member/myinfo")
	HashMap<String, Object> member_myinfo(HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session = request.getSession();
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		String userid = (String)session.getAttribute("user_id");
		if(userid==null) {
			map.put("result", "fail");
			return map;
		}
		
		MemberDto dto = new MemberDto();
		dto.setUser_id(userid);

		MemberDto resultDto = memberService.getInfo(dto);
		map.put("result", "success");
		map.put("memberDto", resultDto);
		//model.addAttribute("memberDto", resultDto);
		return map;
	}
	
	@RequestMapping("/member/login_proc")
	@ResponseBody
	public HashMap<String, String> member_login_proc(MemberDto dto, HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		
		MemberDto resultDto = memberService.getInfo(dto);
		HashMap<String, String> map = new HashMap<String, String>();
		System.out.println(resultDto);

		if(resultDto==null)
		{
			map.put("result", "DB�� ������ �����ϴ�.");	
			map.put("flag", "2");	
		}
		else
		{
			System.out.println(resultDto.getUser_password().equals(dto.getUser_password()));
			if(resultDto.getUser_password().equals(dto.getUser_password()))
			{
				System.out.println("111111111111111111111");
				map.put("result", "�α��ο� ���� �ϼ̽��ϴ�."); //�α׿� ������ ���ǿ� ������ �����Ѵ� 
				map.put("flag", "1");
				session.setAttribute("user_seq", resultDto.getUser_seq());
				session.setAttribute("user_id", resultDto.getUser_id());
				session.setAttribute("user_name", resultDto.getUser_name());
				session.setAttribute("user_mail", resultDto.getUser_mail());
				session.setAttribute("user_phone", resultDto.getUser_phone());
				session.setAttribute("user_level", resultDto.getUser_level());		
				session.setAttribute("user_image1", resultDto.getUser_image1());
				session.setAttribute("user_image2", resultDto.getUser_image2());
				session.setAttribute("user_address1", resultDto.getUser_address1());
				session.setAttribute("user_address2", resultDto.getUser_address2());
				session.setAttribute("user_business", resultDto.getUser_business());
			}
			
			else
			{
				System.out.println("333333333333333");
				map.put("result", "�н����尡 Ʋ�Ƚ��ϴ�");
				map.put("flag", "3");
			}
		}
		
		
		return map;
	}
	
	@RequestMapping("/member/insert")
	public HashMap<String, String> member_insert(MultipartFile file1, MultipartFile file2, MemberDto dto)
	{
		// File Upload
		String uploadDir = fileUploadPath+ "/image" ; 
		
		if(file1!=null)
		{
			try {
				//���ο� ���ϸ��� ��ȯ�Ѵ�(���ϸ��� �ߺ��� �� �ֱ⶧����)
				String filename1=FileUploadUtil.upload(uploadDir, file1);
				dto.setUser_image1(domain +"/"+ uploadDir + "/"+ filename1);
				
				//String filename2=FileUploadUtil.upload(uploadDir, file2);
				//dto.setUser_image2(domain +"/"+ uploadDir + "/"+ filename2);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(file2!=null)
		{
			try {
				//���ο� ���ϸ��� ��ȯ�Ѵ�(���ϸ��� �ߺ��� �� �ֱ⶧����)
				String filename2=FileUploadUtil.upload(uploadDir, file2);
				dto.setUser_image2(domain +"/"+ uploadDir + "/"+ filename2);
				
				//String filename2=FileUploadUtil.upload(uploadDir, file2);
				//dto.setUser_image2(domain +"/"+ uploadDir + "/"+ filename2);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		/////////////////�̹��� ���ε� end /////////////
		System.out.println("userid : " + dto.getUser_id());
		memberService.insert(dto);
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("result", "success");
		return map;
	}
	
	
	
	
	@RequestMapping("/member/logout")
	public HashMap<String,String> member_logout(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		session.invalidate(); //������ ������ ���� 
							  //�ϴ� void�� ó���ϱ� �ߴµ� �α׾ƿ��ϸ� �ٽ� �α��� â���� �����̷�Ʈ �ϰ� �ϰ� �;��
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("result", "success");
		return map;
	}
	
	
	@RequestMapping("/member/findid_proc")
	@ResponseBody
	public HashMap<String, String> member_findid_proc(MemberDto dto)
	{	
		MemberDto findDto = memberService.findId(dto);
		HashMap map = new HashMap<String, String>();
		if (findDto==null)
			map.put("result", "fail");
		else
		{
			map.put("result", findDto.getUser_id());
			map.put("user_id", findDto.getUser_id());
			map.put("user_name", findDto.getUser_name());
		}
		return map;
	}
	
	
	@RequestMapping("/member/findpass_proc")
	@ResponseBody
	public HashMap<String, String> member_findpass_proc(MemberDto dto)
	{	
		System.out.println(dto);
		MemberDto findDto = memberService.findPassword(dto);
		System.out.println(findDto);
		HashMap map = new HashMap<String, String>();
		if (findDto==null)
			map.put("result", "fail");
		else
		{
			map.put("result", findDto.getUser_password());
			map.put("user_id", findDto.getUser_id());
			map.put("user_name", findDto.getUser_name());
		}
		return map;
	}
	
	@RequestMapping("/member/update")
	@ResponseBody
	public HashMap<String, String> member_update(MemberDto dto)
	{	
		memberService.update(dto);
		HashMap map = new HashMap<String, String>();
		map.put("result", "success");	
		return map;
	}
	
}
