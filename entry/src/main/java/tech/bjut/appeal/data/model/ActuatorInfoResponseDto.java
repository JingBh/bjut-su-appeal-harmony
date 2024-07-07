package tech.bjut.appeal.data.model;

public class ActuatorInfoResponseDto {

    private ActuatorBuildInfo build;

    public ActuatorInfoResponseDto() {
        this.build = new ActuatorBuildInfo();
    }

    public ActuatorBuildInfo getBuild() {
        return build;
    }

    public void setBuild(ActuatorBuildInfo build) {
        this.build = build;
    }

    public static class ActuatorBuildInfo {

        private String version;

        public ActuatorBuildInfo() {}

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
