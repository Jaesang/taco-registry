package com.registry.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * 클래스 이름 : ShellCommandUtil
 * 
 * 클래스 설명 : Shell실행, 커맨드라인 실행 유틸
 * 
 * 작성자 정보 : adventurez
 *
 * 버전 정보 	: v1
 *
 * 수정 이력  	: 2016. 02. 19	최초생성
 *
 */
@Component
public class ShellCommandUtil {
	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Private Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	protected static final Logger logger = LoggerFactory.getLogger(ShellCommandUtil.class);

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/	

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/	

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Getter & Setter Method ( DI Method )
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
	
	/**
	 * 커맨드를 실행하고 결과를 반환한다.
	 * @param command	: 커맨드 라인 (ex1: ls -l, ex2: test.sh)
	 * @return
	 * @throws Exception
	 */
	public String excute(String command) {

		String result = "";

		try {
			// 런타임 선언
			Runtime runTime = Runtime.getRuntime();

			// 실행!
			Process process = runTime.exec(command);

			// 기다려
			process.waitFor();

			// 리더로 읽어들여
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			// 사용자가 커스텀한 리더로 가공
			result = this.read(reader);
		} catch( Exception e ) {
			logger.error(e.getMessage());
		}
		return result;
	}

	public void executeOnAsync(String command, ShellCommandUtil.OnCompleteListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result = excute(command);
				if( listener != null ) {
					listener.complete(result);
				}
			}
		}).start();
	}

	/**
	 * 커맨드를 실행하고 결과를 반환한다.
	 * @param command	: 커맨드 라인 (ex1: ls -l, ex2: test.sh)
	 * @return
	 * @throws Exception
	 */
	public String excute(String[] command) {

		String result = "";

		try {

			ProcessBuilder processBuilder = new ProcessBuilder(command);

			// 실행!
			Process process = processBuilder.start();

			// 기다려
			process.waitFor();

			// 리더로 읽어들여
			BufferedReader reader 	= new BufferedReader(new InputStreamReader(process.getInputStream()));

			// 사용자가 커스텀한 리더로 가공
			result = this.read(reader);
		} catch( Exception e ) {
			logger.error(e.getMessage());
		}
		return result;
	}

	public void executeOnAsync(String[] command, ShellCommandUtil.OnCompleteListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result = excute(command);
				if( listener != null ) {
					listener.complete(result);
				}
			}
		}).start();
	}

//	/**
//	 * 커맨드를 실행하고 결과를 반환한다.
//	 * @param command	: 커맨드 라인 (ex1: ls -l, ex2: test.sh)
//	 * @return
//	 * @throws Exception
//	 */
//	public Object excute(String[] command) throws Exception{
//
//		// 런타임 선언
//		Runtime runTime 		= Runtime.getRuntime();
//
//		// 실행!
//		Process process 		= runTime.exec(command);
//
//		// 기다려
//		process.waitFor();
//
//		// 리더로 읽어들여
//		BufferedReader reader 	= new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//		// 사용자가 커스텀한 리더로 가공
//		Object result			= this.read(reader);
//
//		return result;
//	}
	
	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Implement Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Override Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/	

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
	
	/**
	 * 커맨드를 실행한 후 결과를 가공하기 위한 메소드
	 * Override하여 원하는 형태로 가공하여 반환한다.
	 * @param reader	: 커맨드 실행결과
	 * @return
	 * @throws Exception
	 */
	protected String read(BufferedReader reader) throws Exception{
		
		// 라인 한줄
		String line 			= "";
		
		// 라인 번호
		int idx 				= 0;

		// 결과
		StringBuffer output 	= new StringBuffer();

		// 리더로 읽어들이면서
		while ((line = reader.readLine())!= null) {

			// 첫번째 라인이 아니라면
			if( idx > 0 ) {

				// 줄바꿈 추가
				output.append( "\n" );
			}

			// 라인 추가
			output.append( line );

			// 라인 번호 증가
			idx++;		
		}
		
		// 반환
		return output.toString();
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Private Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/


	public interface OnCompleteListener {
		void complete(String result);
	}
}
