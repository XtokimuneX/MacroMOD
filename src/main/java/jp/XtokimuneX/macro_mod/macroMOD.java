package jp.xtokimunex.macro_mod;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("macro_mod")
public class macroMOD
{
    //ログ？
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();


    //メッセージを管理するリスト
    private List<String> tt;
    //キーバインドのリスト
    private List<KeyBinding> kb;

    public macroMOD() {
        // セットアップ
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        //イベントの登録
        EVENT_BUS.addListener(EventPriority.LOWEST, this::onKey);

        // Register ourselves for server and other game events we are interested in
        EVENT_BUS.register(this);
    }

    //メイン部分
    private void setup(final FMLCommonSetupEvent event) {

        //設定ファイルの読み込み
        SettingRead();

        //再読み込みの登録
        //ReloadKey = new KeyBinding("設定再読み込み", -1, "秋宗屋謹製マクロMOD");
        //ClientRegistry.registerKeyBinding(ReloadKey);


    }

    //キーイベント
    private void onKey(InputEvent.KeyInputEvent evt) {
        //設定再読み込みは先にする
//        if(ReloadKey.isPressed()) {
//            Minecraft.getInstance().player.sendChatMessage("test");
//            return;
//        }

        //普通のマクロはこちら
        //kbとttでナンバーにズレがあるので注意
        for (int i=0;i<kb.size();i++)
        {
            try {
                if (kb.get(i).isPressed()) {
                    Minecraft.getInstance().player.sendChatMessage(tt.get(i + 1));
                }
            }catch (IndexOutOfBoundsException e){
                Minecraft.getInstance().player.sendChatMessage("");
            }
        }

    }

    private void SettingRead()
    {
        tt= new ArrayList<>();
        kb= Lists.newArrayList();

        try {
            // ファイルのパスを指定する
            File file = new File("マクロ設定.txt");

            // ファイルが存在しない場合に作成する
            if (!file.exists()) {
                //作るメソッド
                TextCreate();

                // それでもファイルが存在しない場合に例外が発生するので確認する
                if (!file.exists()) {
                    tt.add("ファイルが読み込めませんでした。");
                    return;
                }
            }

            // BufferedReaderクラスのreadLineメソッドを使って1行ずつ読み込み表示する
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                tt.add(data);
            }

            //読み込んだリストを元にマクロ枠を作っていく
            //リストの1行目はファイルの1行目であり注意書き
            //なのでリストとマクロ枠のナンバーが１つずれる
            for(int i=1;i<tt.size();i++)
            {
                if(!tt.get(i).isEmpty())
                {
                    KeyBinding k = new KeyBinding("マクロ"+i, -1, "秋宗屋謹製マクロMOD");
//                    KeyBinding k = new KeyBinding(s, -1, "秋宗屋謹製マクロMOD");
                    kb.add(k);
                    ClientRegistry.registerKeyBinding(k);
                }
            }

            // 最後にファイルを閉じてリソースを開放する
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void TextCreate() {
        File file = new File("マクロ設定.txt");

        try {
            // 文字コードを指定する
            PrintWriter p_writer = new PrintWriter(new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)));

            //ファイルに文字列を書き込む
            p_writer.println("※UTF-8で保存してください。1行が1つのマクロになります。何行でもいけます。");
            p_writer.println("マクロテスト1");
            p_writer.println("マクロテスト2");
            p_writer.println("マクロテスト3");
            p_writer.println("マクロテスト4");
            p_writer.println("マクロテスト5");

            //ファイルをクローズする
            p_writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}