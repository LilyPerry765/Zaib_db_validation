    private void updateGraph(ActionRequest actionRequest,
            ActionResponse actionResponse) {
        DBManager DBase = new DBManager();
        Connection con = DBase.getConnection();
        String graph_id = actionRequest.getParameter("graph_id");
        actionResponse.setRenderParameter("graph_id", graph_id);

        String name = actionRequest.getParameter("name");
        String description = actionRequest.getParameter("description");
        String server_id = actionRequest.getParameter("server_id");
        String xlabel = actionRequest.getParameter("xlabel");
        String ylabel = actionRequest.getParameter("ylabel");
        String timeframe = actionRequest.getParameter("timeframe");
        String mbean = actionRequest.getParameter("mbean");
        String dataname1 = actionRequest.getParameter("dataname1");
        String data1operation = actionRequest.getParameter("data1operation");
        String operation = actionRequest.getParameter("operation");
        int archive = 0;
        if (actionRequest.getParameter("showArchive") != null
                && actionRequest.getParameter("showArchive").equals("on")) {
            archive = 1;
        }

        if (operation.equals("other")) {
            operation = actionRequest.getParameter("othermath");
        }
        String dataname2 = actionRequest.getParameter("dataname2");
        String data2operation = actionRequest.getParameter("data2operation");
        if (data2operation == null)
            data2operation = "A";
        try {
            PreparedStatement pStmt = con
                    .prepareStatement("UPDATE graphs SET server_id="
                            + server_id
                            + ", name='"
                            + name
                            + "', description='"
                            + description
                            + "', timeframe="
                            + timeframe
                            + ", mbean='"
                            + mbean
                            + "', dataname1='"
                            + dataname1
                            + "', xlabel='"
                            + xlabel
                            + "', ylabel='"
                            + ylabel
                            + "', data1operation='"
                            + data1operation
                            + "', operation='"
                            + operation
                            + "', data2operation='"
                            + data2operation
                            + "', dataname2='"
                            + dataname2
                            + "', warninglevel1=0, warninglevel2=0, modified=CURRENT_TIMESTAMP, archive="
                            + archive + " WHERE graph_id=" + graph_id);
            pStmt.executeUpdate();
            con.close();
            actionResponse.setRenderParameter("message",
                    "Graph " + name
                            + " has been updated.");
            return;

        } catch (Exception e) {
            actionResponse.setRenderParameter("message",
                    "Error editing graph "
                            + e.getMessage());
            return;
        }
    }

