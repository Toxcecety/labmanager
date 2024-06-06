package fr.utbm.ciad.labmanager.data.publication;

public class PublicationImp extends Publication{

    public PublicationImp() {
        super();
    }

    @Override
    public boolean isRanked() {
        return false;
    }

    @Override
    public String getWherePublishedShortDescription() {
        return "";
    }

    @Override
    public String getPublicationTarget() {
        return "";
    }
}
