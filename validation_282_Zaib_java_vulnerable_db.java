    private ScimGroupExternalMember getExternalGroupMap(final String groupId,
                                                        final String externalGroup,
                                                        final String origin)
                    throws ScimResourceNotFoundException {
        try {
            ScimGroupExternalMember u = jdbcTemplate.queryForObject(GET_GROUPS_WITH_EXTERNAL_GROUP_MAPPINGS_SQL,
                            rowMapper, groupId, origin, externalGroup);
            return u;
        } catch (EmptyResultDataAccessException e) {
            throw new ScimResourceNotFoundException("The mapping between groupId " + groupId + " and external group "
                            + externalGroup + " does not exist");
        }
    }

