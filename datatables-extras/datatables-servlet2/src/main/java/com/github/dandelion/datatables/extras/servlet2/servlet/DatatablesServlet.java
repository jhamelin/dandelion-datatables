/*
 * [The "BSD licence"]
 * Copyright (c) 2012 Dandelion
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of Dandelion nor the names of its contributors 
 * may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.dandelion.datatables.extras.servlet2.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dandelion.datatables.core.asset.WebResources;
import com.github.dandelion.datatables.core.cache.AssetCache;
import com.github.dandelion.datatables.core.util.DandelionUtils;

/**
 * Dandelion-datatables servlet compatible whith the servlet 2.x API.
 * 
 * @author Thibault Duchateau
 */
public class DatatablesServlet extends HttpServlet {
	private static final long serialVersionUID = 4971523176859296399L;

	// Logger
	private static Logger logger = LoggerFactory.getLogger(DatatablesServlet.class);

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.debug("DataTables servlet captured GET request {}", request.getRequestURI());
		
		// Common response header
		// TODO adapt caching behaviour depending on the file nature (e.g. plugin)
		response.setHeader("Cache-Control","no-cache");
		
		// Get requested file name
		StringBuffer resourceUrl = request.getRequestURL();
		String resourceName = resourceUrl.substring(resourceUrl.lastIndexOf("/") + 1);
		String fileContent = null;

		String mainKey = request.getParameter("c") + "|" + request.getParameter("id");
		String type = request.getParameter("t");
		
		if(DandelionUtils.isDevModeEnabled() || AssetCache.cache.containsKey(mainKey)){
			
			// Depending on its type, different content is served
			if(resourceName.endsWith("js")){
				
				// Set header properties
				response.setContentType("application/javascript");
				
				if("main".equals(type)){
					fileContent = ((WebResources) AssetCache.cache.get(mainKey)).getMainJsFile().getContent();
				}
				else{
					fileContent = ((WebResources) AssetCache.cache.get(mainKey)).getJavascripts().get(resourceName).getContent();
				}
			}
			else if(resourceName.endsWith("css")){
				
				// Set header properties
				response.setContentType("text/css");
				
				fileContent = ((WebResources) AssetCache.cache.get(mainKey)).getStylesheets().get(resourceName).getContent();
			}
			
			// Write the content in the response
			response.getWriter().write(fileContent);
		}
		else{
			throw new ServletException("The asset should have been generated!!!!");
		}
	}
}