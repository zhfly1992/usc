package com.run.base.test.cases;

public class PackageTest {

	/*@SuppressWarnings("resource")*/
	public static void main(String[] args) {
		
		//System.out.println(RegexUtil.validateEmail("12345@qq.com"));
		/*try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "spring-test-invoker.xml" });
			context.start();
			MetaDataPackageCrudRpcService metaDataPackageCrudRpcService = (MetaDataPackageCrudRpcService) context
					.getBean("metaDataPackageCrudRpcService");
			MetaDataPackage metaDataPackage=new MetaDataPackage();
			metaDataPackage.setPackageName("com.device");
			metaDataPackage.setPackageDescription("设备分包");
			RpcResponse<MetaDataPackage> savePackage = metaDataPackageCrudRpcService.savePackage(metaDataPackage);
			System.out.println(savePackage.isSuccess());
			System.out.println(savePackage.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}

}
