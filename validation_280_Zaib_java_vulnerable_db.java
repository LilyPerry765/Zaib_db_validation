   public void handleDialog(ActionRequest req, ActionResponse resp) throws IOException, PortletException {
      List<String> lines = new ArrayList<String>();
      req.getPortletSession().setAttribute("lines", lines);

      lines.add("handling dialog");
      StringBuilder txt = new StringBuilder(128);

      String clr = req.getActionParameters().getValue("color");
      txt.append("Color: ").append(clr);
      lines.add(txt.toString());
      LOGGER.fine(txt.toString());

      resp.getRenderParameters().setValue("color", clr);

      txt.setLength(0);
      Part part = null;
      try {
         part = req.getPart("file");
      } catch (Throwable t) {}
      
      if ((part != null) && (part.getSubmittedFileName() != null) && 
            (part.getSubmittedFileName().length() > 0)) {
         txt.append("Uploaded file name: ").append(part.getSubmittedFileName());
         txt.append(", part name: ").append(part.getName());
         txt.append(", size: ").append(part.getSize());
         txt.append(", content type: ").append(part.getContentType());
         lines.add(txt.toString());
         LOGGER.fine(txt.toString());
         txt.setLength(0);
         txt.append("Headers: ");
         String sep = "";
         for (String hdrname : part.getHeaderNames()) {
            txt.append(sep).append(hdrname).append("=").append(part.getHeaders(hdrname));
            sep = ", ";
         }
         lines.add(txt.toString());
         LOGGER.fine(txt.toString());

         // Store the file in a temporary location in the webapp where it can be served. 
         // Note that this is, in general, not what you want to do in production, as
         // there can be problems serving the resource. Did it this way for a 
         // quick solution that doesn't require additional Tomcat configuration.

         try {
            String path = req.getPortletContext().getRealPath(TMP);
            File dir = new File(path);
            lines.add("Temp path: " + dir.getCanonicalPath());
            if (!dir.exists()) {
               lines.add("Creating directory. Path: " + dir.getCanonicalPath());
               Files.createDirectories(dir.toPath());
            }
            String fn = TMP + part.getSubmittedFileName();
            lines.add("Temp file: " + fn);
            path = req.getPortletContext().getRealPath(fn);
            File img = new File(path);
            if (img.exists()) {
               lines.add("deleting existing temp file.");
               img.delete();
            }
            InputStream is = part.getInputStream();
            Files.copy(is, img.toPath(), StandardCopyOption.REPLACE_EXISTING);

            resp.getRenderParameters().setValue("fn", fn);
            resp.getRenderParameters().setValue("ct", part.getContentType());

         } catch (Exception e) {
            lines.add("Exception doing I/O: " + e.toString());
         }
      } else {
         lines.add("file part was null");
      }

   }

   @RenderMethod(portletNames = "MultipartPortlet")
