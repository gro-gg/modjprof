package ch.puzzle.modjprof.control;
/**
 * Copyright 2015 The modjprof Project Developers. See the COPYRIGHT file at the top-level directory of this distribution and at
 * https://github.com/gro-gg/modjprof/blob/master/COPYRIGHT.
 *
 * This file is part of modjprof Project. It is subject to the license terms in the LICENSE file found in the top-level directory
 * of this distribution and at https://github.com/gro-gg/modjprof/blob/master/LICENSE. No part of modjprof Project, including this
 * file, may be copied, modified, propagated, or distributed except according to the terms contained in the LICENSE file.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See LICENSE file for more details.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "DownloadServlet", urlPatterns = { "/downloadfile" })
public class DownloadServlet extends HttpServlet {

    private static final long serialVersionUID = 3416036049883045920L;

    private final static Logger LOGGER = Logger.getLogger(DownloadServlet.class.getName());

    private static final int BUFFER_SIZE = 4096;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String traceFile = request.getParameter("file");
        LOGGER.info("Download started for " + traceFile);
        InputStream inputStream = null;
        ServletOutputStream outputSteam = response.getOutputStream();
        try {
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment;filename=" + (new File(traceFile)).getName());

            int bytesRead = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            inputStream = new FileInputStream(traceFile);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputSteam.write(buffer, 0, bytesRead);
            }
            outputSteam.flush();
        } catch (Exception e) {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(outputSteam));
            e.printStackTrace(out);
            out.close();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (outputSteam != null) {
                    outputSteam.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet to download modjprof trace files";
    }

}
