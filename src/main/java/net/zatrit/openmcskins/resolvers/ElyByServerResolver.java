package net.zatrit.openmcskins.resolvers;

public class ElyByServerResolver extends SimpleServerResolver {
    public ElyByServerResolver() {
        super("http://skinsystem.ely.by");
    }

    @Override
    public String getName() {
        return "ely.by";
    }
}
