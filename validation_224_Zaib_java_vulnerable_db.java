    public String toString() {

        StringBuilder sb = new StringBuilder("<user username=\"");
        sb.append(RequestUtil.filter(username));
        sb.append("\" password=\"");
        sb.append(RequestUtil.filter(password));
        sb.append("\"");
        if (fullName != null) {
            sb.append(" fullName=\"");
            sb.append(RequestUtil.filter(fullName));
            sb.append("\"");
        }
        synchronized (groups) {
            if (groups.size() > 0) {
                sb.append(" groups=\"");
                int n = 0;
                Iterator<Group> values = groups.iterator();
                while (values.hasNext()) {
                    if (n > 0) {
                        sb.append(',');
                    }
                    n++;
                    sb.append(RequestUtil.filter(values.next().getGroupname()));
                }
                sb.append("\"");
            }
        }
        synchronized (roles) {
            if (roles.size() > 0) {
                sb.append(" roles=\"");
                int n = 0;
                Iterator<Role> values = roles.iterator();
                while (values.hasNext()) {
                    if (n > 0) {
                        sb.append(',');
                    }
                    n++;
                    sb.append(RequestUtil.filter(values.next().getRolename()));
                }
                sb.append("\"");
            }
        }
        sb.append("/>");
        return (sb.toString());

    }


