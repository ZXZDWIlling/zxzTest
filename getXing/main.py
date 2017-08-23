from selenium import webdriver
from selenium.webdriver import ActionChains
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as ec

driver = webdriver.Firefox()
driver.implicitly_wait(30)
driver.get('http://www.mhchina.net/index.html')

# 点击普世教会
WebDriverWait(driver, 30, 0.5).until(ec.visibility_of_all_elements_located((By.XPATH, '*')))
ActionChains(driver).move_to_element(driver.find_element_by_xpath("//*[@id='qm0']/a[1]/span/span[5]")).perform()
driver.find_element_by_link_text('普世教會').click()

# 功课列表共有11列
WebDriverWait(driver, 30, 0.5).until(ec.frame_to_be_available_and_switch_to_it((By.TAG_NAME, 'iframe')))
rows = driver.find_elements_by_xpath("html/body/table/tbody/tr/td[0]")







