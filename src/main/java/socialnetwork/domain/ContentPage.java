package socialnetwork.domain;

public class ContentPage {
    private int sizePage;
    private int numberPage;

    /**
     * Constructor that creates a new ContentPage
     * @param sizePage int, representing the size of the current Page
     * @param numberPage int, representing the number of the current Page
     */
    public ContentPage(int sizePage, int numberPage) {
        this.sizePage = sizePage;
        this.numberPage = numberPage;
    }

    /**
     * @return int, representing the size of the current Page
     */
    public int getSizePage() {
        return sizePage;
    }

    /**
     * @param sizePage int, representing the size of the new current Page
     */
    public void setSizePage(int sizePage) {
        this.sizePage = sizePage;
    }

    /**
     * @return int, representing the number of the current Page
     */
    public int getNumberPage() {
        return numberPage;
    }

    /**
     * @param numberPage int, representing the number of the new current Page
     */
    public void setNumberPage(int numberPage) {
        this.numberPage = numberPage;
    }

    /**
     * Method that sets the current Page to the next Page
     */
    public void nextPage() {
        numberPage++;
    }

    /**
     * Method that sets the current Page to the previous Page
     */
    public void previousPage() {
        numberPage--;
    }

    /**
     * Method that sets the current Page to the first Page
     */
    public void setToFirstPage() {
        numberPage = 1;
    }
}
