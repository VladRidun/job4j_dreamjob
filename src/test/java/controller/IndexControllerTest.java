package controller;

import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.controller.IndexController;

import static org.assertj.core.api.Assertions.*;

class IndexControllerTest {

    private IndexController indexController;

    @Test
    public void whenRequestIndexPageThenGetPageIndex() {
        indexController = new IndexController();
        var view = indexController.getIndex();
        assertThat(view).isEqualTo("index");
    }

}