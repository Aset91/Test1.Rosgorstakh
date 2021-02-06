import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TaskTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void before() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        System.setProperty("webdriver.chrome.driver", "webdriver/chromedriver.exe");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(30, SECONDS);
        driver.manage().timeouts().implicitlyWait(10, SECONDS);
        wait = new WebDriverWait(driver, 10, 1000);


        //переходим по ссылке
        String baseUrl = "http://www.rgs.ru";

        driver.get(baseUrl);
    }

    @Test
    public void test() {
        //Выбрать Меню
        String menuButton = "//a[@data-toggle= 'dropdown' and @class='hidden-xs']";
        WebElement menuButtonList = driver.findElement(By.xpath(menuButton));

            menuButtonList.click();

            //Компаниям > Здоровье > Добровольное медицинское страхование
            String forCompanies = "//a[contains(text(),'Компаниям')]";
            WebElement forCompaniesButton = driver.findElement(By.xpath(forCompanies));
            forCompaniesButton.click();

            String health = "//a[@href='/products/juristic_person/health/index.wbp' and contains(@class, 'navigation')]";
            waitUntilElementToBeVisible(By.xpath(health));
            WebElement healthButton = driver.findElement(By.xpath(health));

            healthButton.click();

            String insurance = "//a[contains(text(), 'медицинское страхование')]";
            WebElement insuranceButton = driver.findElement(By.xpath(insurance));
            insuranceButton.click();

            // Проверить наличие заголовка - Добровольное медицинское страхование
            Assert.assertEquals("Заголовок неверный", "Добровольное медицинское страхование в Росгосстрахе", driver.getTitle());


            //Нажать на кнопку - Отправить заявку
            String application = "//a[contains(text(),'Отправить заявку')]";
            WebElement pushApplicationButton = driver.findElement(By.xpath(application));
            pushApplicationButton.click();

            // Проверить, что открылась страница , на которой присутствует текст - Заявка на добровольное медицинское страхование
            String applicationFormTitle = "//b[text()= 'Заявка на добровольное медицинское страхование']";
            waitUntilElementToBeVisible(By.xpath(applicationFormTitle));
            WebElement appTitle = driver.findElement(By.xpath(applicationFormTitle));
            Assert.assertEquals("Страница не содержит искомого текста", "Заявка на добровольное медицинское страхование", appTitle.getText());

            //.Заполнить поля
            //	Имя, Фамилия, Отчество, Регион, Телефон,
            //	Эл. почта - qwertyqwerty,
            //	Комментарии, Я согласен на обработку
            String inputField = "//input[@name='%s']";
            fillInputField(driver.findElement(By.xpath(String.format(inputField, "LastName"))), "Иванов");
            fillInputField(driver.findElement(By.xpath(String.format(inputField, "FirstName"))), "Василий");
            fillInputField(driver.findElement(By.xpath(String.format(inputField, "MiddleName"))), "Иванович");
            driver.findElement(By.xpath("//select[@name='Region']")).click();
            driver.findElement(By.xpath("//select[@name='Region']")).sendKeys(Keys.DOWN);
            driver.findElement(By.xpath("//select[@name='Region']")).click();
            driver.findElement(By.xpath("//select[@name='Region']")).sendKeys(Keys.TAB);
            driver.findElement(By.xpath("//select[@name='Region']")).sendKeys(Keys.TAB);

            String phonePath = "//input[contains(@data-bind, 'Phone')]";
            fillInputFieldPhone(driver.findElement(By.xpath(phonePath)), "9268769078");
            fillInputField(driver.findElement(By.xpath(String.format(inputField, "Email"))), "qwertyqwerty");
            fillInputFieldDate(driver.findElement(By.xpath(String.format(inputField, "ContactDate"))), "11052021");
            driver.findElement(By.xpath("//textarea[@name='Comment']")).sendKeys("...");

            String checkBoxPath = "//input[@class='checkbox']";
            WebElement pressCheckBox = driver.findElement(By.xpath(checkBoxPath));
            pressCheckBox.click();

            //Нажать Отправить

            driver.findElement(By.xpath("//button[@id='button-m']")).click();

            //Проверить, что у Поля - Эл. почта присутствует сообщение об ошибке - Введите корректный email
            checkErrorMessageAtField(driver.findElement(By.xpath(String.format(inputField, "Email"))), "Некорректное значение");

        }

    @After
    public void after() {
        driver.quit();
    }

    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javaScriptExecutor = (JavascriptExecutor) driver;
        javaScriptExecutor.executeScript("arguments[0].scrollIntoView(true)", element);
    }
        private void waitUntilElementToBeClickable(WebElement element) {
            wait.until(ExpectedConditions.elementToBeClickable(element));
        }
        private void waitUntilElementToBeClickable(By locator){
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        }
        private void waitUntilElementToBeVisible(By locator) {
            wait.until(ExpectedConditions.visibilityOfElementLocated((locator)));
        }
        private void waitUntilElementToBeVisible(WebElement element) {
            wait.until(ExpectedConditions.visibilityOf(element));
        }
        private void fillInputField(WebElement element, String value) {
            //scrollToElementJs(element);
            waitUntilElementToBeClickable(element);
            element.click();
            element.sendKeys(value);
            Assert.assertEquals("Поле было заполнено некорректно",
                value, element.getAttribute("value"));
            element.sendKeys(Keys.TAB);
        }

    private void fillInputFieldPhone(WebElement element, String value) {
        //scrollToElementJs(element);
        waitUntilElementToBeClickable(element);
        element.click();
        element.sendKeys(value);
        String phoneValue = "+7 (" + value.substring(0, 3) + ") " + value.substring(3,6) + "-" + value.substring(6,8) + "-" + value.substring(8, 10);
        Assert.assertEquals("Поле было заполнено некорректно",
                phoneValue, element.getAttribute("value"));
        element.sendKeys(Keys.TAB);
    }

    private void fillInputFieldDate(WebElement element, String value) {
        //scrollToElementJs(element);
        waitUntilElementToBeClickable(element);
        element.click();
        element.sendKeys(value);
        String dateValue = value.substring(0, 2) + "." + value.substring(2, 4) + "." + value.substring(4,8);
        Assert.assertEquals("Поле было заполнено некорректно",
                dateValue, element.getAttribute("value"));
        element.sendKeys(Keys.TAB);
    }

        private void checkErrorMessageAtField(WebElement element, String errorMessage) {
            element = element.findElement(By.xpath("/./..//span"));
            Assert.assertEquals("Проверка ошибки у поля не пройдена",
            errorMessage, element.getText());
        }

    }


