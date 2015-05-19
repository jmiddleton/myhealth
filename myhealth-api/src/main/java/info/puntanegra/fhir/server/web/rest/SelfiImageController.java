package info.puntanegra.fhir.server.web.rest;

import info.puntanegra.fhir.server.web.rest.dto.FileMeta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

//@RestController
//@RequestMapping("/api")
public class SelfiImageController {

	public static final String SUCCESS = "SUCCESS";

	private static Map<String, FileMeta> files = new HashMap<String, FileMeta>();

//	@RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
	public @ResponseBody void upload(MultipartHttpServletRequest request, HttpServletResponse response) {
		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf = null;
		String ihi = getPcehrIHI(request.getParameter("ihi"));

		while (itr.hasNext()) {
			mpf = request.getFile(itr.next());

			FileMeta fileMeta = new FileMeta();
			fileMeta.setFileName(mpf.getOriginalFilename());
			fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
			fileMeta.setFileType(mpf.getContentType());

			try {
				fileMeta.setBytes(mpf.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			files.put(ihi, fileMeta);
			break;
		}
	}

//	@RequestMapping(value = "/getImage/{ihi}", method = RequestMethod.GET)
	public void getImage(HttpServletResponse response, @PathVariable String ihi) {

		try {
			ihi = getPcehrIHI(ihi);

			if (ihi == null) {
				return;
			}

			FileMeta getFile = files.get(ihi);
			if (getFile != null) {
				response.setContentType(getFile.getFileType());
				response.setHeader("Content-disposition", "attachment; filename=\"" + getFile.getFileName() + "\"");

				FileCopyUtils.copy(getFile.getBytes(), response.getOutputStream());
			} else {
				getDefaultImage(response);
			}
		} catch (Exception re) {
			getDefaultImage(response);
		}
	}

	private void getDefaultImage(HttpServletResponse response) {
		try {
			response.setContentType("image/png");
			response.setHeader("Content-disposition", "attachment; filename=\"empty.png\"");

			FileCopyUtils.copy(this.getClass().getResourceAsStream("/META-INF/empty.png"), response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getPcehrIHI(String ihi) {
		return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	protected String handleError(String message) {
		return handleResponse("ERROR", message);
	}

	protected String handleResponse(String status, String message) {
		if (message != null) {
			message = ", \"detail\": \"" + message + "\"";
		} else {
			message = "";
		}
		return "{\"status\":\"" + status + "\"" + message + "}";
	}

}