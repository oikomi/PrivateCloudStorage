
local BASE_DIR = "/mh/PrivateCloudStorage/data"

local lfs = require("lfs")
local cjson = require("cjson")

local file_list_data = {}

local function get_type(path)
	return lfs.attributes(path).mode
end

local function get_size(path)
	return lfs.attributes(path).size
end

local function is_dir(path)
	return get_type(path) == "directory"
end

local function findx(str,x)
	for i = 1,#str do
		if string.sub(str,-i,-i) == x then
			return -i
		end
	end
end

local function get_name(str)
	return string.sub(str,findx(str,"/") + 1,-1)
end

local function scan_dir(relative_path)
--[[ 	local table = "{"
	for file in lfs.dir(path) do
		p = path .. "/" .. file
		if file ~= "." and file ~= '..' then
			if isDir(p) then
				s = "{'text':'".. file .. "','type':'" .. getType(p) .. "','path':'" .. p .. "','children':[]},"
			else
				s = "{'text':'".. file .. "','type':'" .. getType(p) .. "','path':'" .. p .. "','size':" .. getSize(p) .. ",'leaf':true},"
			end  
			table = table .. s    
		end
	end
	table = table .. "}"
	return table ]]
	
	--t[path] = {}
	
	local path = BASE_DIR .. relative_path
	
	for f in lfs.dir(path) do
		p = path .. "/" .. f
		t = {}
		if f ~= "." and f ~= '..' then
			if is_dir(p) then
				t["name"] = f
				t["type"] = "dir"
				t["size"] = "0kb"
				t["path"] = relative_path  .. f
				table.insert(file_list_data, t)
				--scan_dir(p, t[f])
			else
				t["name"] = f
				t["type"] = "file"
				t["size"] = get_size(p) .. "b"
				t["path"] = relative_path .. f
				table.insert(file_list_data, t)
			end  
			
		end
	end
end


local function get_server_file_list(relative_path)
	scan_dir(relative_path)
	ngx.log(ngx.ERR, cjson.encode(file_list_data))
	ngx.print(cjson.encode(file_list_data))
end


local http_method = ngx.req.get_method()

if http_method == "GET" then
	ngx.log(ngx.ERR, "a GET req")
	local args = ngx.req.get_uri_args()
	for key, val in pairs(args) do
		if type(val) == "table" then
			ngx.log(ngx.ERR, "not support")
			ngx.exit(403)
		else
			if key == "action" then
				if val == "get_server_file_list" then
					get_server_file_list(args["path"])
				end
			end
		end
	end
end


if http_method == "POST" then
	ngx.log(ngx.ERR, "a POST req")
end