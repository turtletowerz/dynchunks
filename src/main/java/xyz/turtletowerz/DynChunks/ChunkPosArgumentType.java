// package xyz.turtletowerz.DynChunks;

// import java.util.Arrays;
// import java.util.Collection;
// import java.util.concurrent.CompletableFuture;

// import com.ibm.icu.impl.locale.XLikelySubtags;
// import com.mojang.brigadier.StringReader;
// import com.mojang.brigadier.arguments.ArgumentType;
// import com.mojang.brigadier.arguments.IntegerArgumentType;
// import com.mojang.brigadier.context.CommandContext;
// import com.mojang.brigadier.exceptions.CommandSyntaxException;
// import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
// import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
// import com.mojang.brigadier.suggestion.Suggestions;
// import com.mojang.brigadier.suggestion.SuggestionsBuilder;

// import net.minecraft.command.CommandSource;
// import net.minecraft.server.command.CommandManager;
// import net.minecraft.server.command.ServerCommandSource;
// import net.minecraft.text.Text;
// import net.minecraft.util.Identifier;
// import net.minecraft.util.math.ChunkPos;

// public class ChunkPosArgumentType implements ArgumentType<ChunkPos> {
//     private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "1 16", "300 -42");
//     private static final SimpleCommandExceptionType INVALID_CHUNKPOS_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Invalid chunk position"));
//     // private final int x;
//     // private final int z;

//     // public ChunkPosArgumentType(int x, int z) {
//     //     this.x = x;
//     //     this.z = z;
//     // }

//     public static ChunkPosArgumentType chunkpos() {
//         return new ChunkPosArgumentType();
//     }

//     public static ChunkPos getChunkPos(CommandContext<ServerCommandSource> context, String name) {
//         return context.getArgument(name, ChunkPos.class);
//     }

//     @Override
//     public ChunkPos parse(StringReader reader) throws CommandSyntaxException {
//         IntegerArgumentType intType = IntegerArgumentType.integer();
//         int i = reader.getCursor();
//         Integer coordinateArgument = intType.parse(reader);
//         if (!reader.canRead() || reader.peek() != ' ') {
//             reader.setCursor(i);
//             throw INVALID_CHUNKPOS_EXCEPTION.createWithContext(reader);
//         }
//         reader.skip();
//         Integer coordinateArgument2 = intType.parse(reader);
//         //return new DefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3);
//         return new ChunkPos(coordinateArgument, coordinateArgument2);
//     }

//     @Override
//     public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
//         // if (context.getSource() instanceof CommandSource) {
//         //     String string = builder.getRemaining();
//         //     Collection<CommandSource.RelativePosition> collection = ((CommandSource)context.getSource()).getPositionSuggestions();
//         //     return CommandSource.suggestPositions(string, collection, builder, CommandManager.getCommandValidator(this::parse));
//         // }
//         return Suggestions.empty();
//     }

//     @Override
//     public Collection<String> getExamples() {
//         return EXAMPLES;
//     }

//     // @Override
//     // public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
//     //     return this.parse(reader);
//     // }

//     /*
//         public static CompletableFuture<Suggestions> suggestPositions(String remaining, Collection<ChunkPos> candidates, SuggestionsBuilder builder, Predicate<String> predicate) {
//             ArrayList<String> list;
//             String[] strings;
//             block3: {
//                 block2: {
//                     list = Lists.newArrayList();
//                     if (!Strings.isNullOrEmpty(remaining)) break block2;
//                     for (ChunkPos chunkpos : candidates) {
//                         String string = chunkpos.x + " " + chunkpos.z;
//                         if (!predicate.test(string)) continue;
//                         list.add(Integer.toString(chunkpos.x));
//                         list.add(chunkpos.x + " " + chunkpos.z);
//                         list.add(string);
//                     }
//                     break block3;
//                 }
//                 strings = remaining.split(" ");
//                 if (strings.length != 1) break block3;
//                 for (ChunkPos chunkpos : candidates) {
//                     String string2 = strings[0] + " " + chunkpos.z;
//                     if (!predicate.test(string2)) continue;
//                     list.add(string2);
//                 }
//             }
//             return CommandSource.suggestMatching(list, builder);
//         }
//     */
// }
