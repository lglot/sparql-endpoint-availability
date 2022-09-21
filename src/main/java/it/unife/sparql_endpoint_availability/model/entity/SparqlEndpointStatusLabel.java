package it.unife.sparql_endpoint_availability.model.entity;

public enum SparqlEndpointStatusLabel {
    ACTIVE("Active"),
    INACTIVE_LESSDAY("Inactive less than a day"),
    INACTIVE_MOREDAY("Inactive more than a day"),
    INACTIVE_MOREWEEK("Inactive more than a week"),
    GENERAL_INACTIVE("General inactive"),
    UNKNOWN("Unknown");

    private final String label;

    SparqlEndpointStatusLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
